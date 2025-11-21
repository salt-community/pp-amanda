# Data Handling

## DynamoDB Tables

### Sessions
### Games
### TopScores

#### TTL - behavior
#### GSI - behavior

### In-memory-storage (player, cell)

# Data Handling & Storage Architecture

The QuickR system uses a combination of **DynamoDB** (persistent storage) and **in-memory storage** 
(ephemeral game state) to support fast gameplay, real-time updates, and reliable persistence.

This document describes how data flows through the system and how each table and storage mechanism is used.

---

# DynamoDB Tables

QuickR uses **three** DynamoDB tables:

- `Sessions`
- `Games`
- `TopScores`

Each serves a distinct purpose in the system architecture.

---

## Sessions Table

**Purpose:**  
Stores lightweight session information created by the Lobby service.

**When written:**  
Immediately when a new session is generated via Lobby (`/lobby/session`).

**Schema:**

| Field       | Type   | Description |
|-------------|--------|-------------|
| `sessionId` | PK     | 4-digit code created by Lobby |
| `createdAt` | String | ISO-8601 timestamp |
| `expiredAt` | Number | TTL (Unix epoch seconds) |

**Notes:**

- Contains **no player data**
- Exists only to validate session existence
- Old sessions are automatically deleted via TTL
- Queried only by Lobby and Lambda

---

## Games Table

**Purpose:**  
Holds all game metadata + players + scores.

**Written by:**

- Lambda → creates base game record
- GameService → updates joinDeadline, type, TTL
- GameRepository → adds players, writes final scoreboard

**Schema:**

| Field          | Type                | Description |
|----------------|---------------------|-------------|
| `gameId`       | PK (String)         | Unique UUID |
| `sessionId`    | String (GSI PK)     | Lookup via `SessionIndex` |
| `type`         | Enum (String)       | e.g., `"REACTION"` |
| `joinDeadline` | String (ISO-8601)   | Controls join window |
| `ttl`          | Number              | TTL for cleanup |
| `players`      | Map<String,Number>  | Player → score |

**Key characteristics:**

- GameService frequently updates player scores (`UpdateItem`)
- `players` map uses atomic, idempotent writes (`if_not_exists`)
- The table stores **durable state**, not real-time state

---

## TopScores Table

**Purpose:**  
Stores high scores across all games.

**Used by:**

- GameService → when a game ends
- `/game/top-list` endpoint

**Schema:**

| Field        | Type   |
|--------------|--------|
| `pk`         | `"TOP"` |
| `sk`         | UUID (sort key) |
| `playerName` | String |
| `score`      | Number |

**Notes:**

- Simple append-only store
- Queried using a full table scan (small data volume)
- Returned sorted by score (descending)

---

# TTL Behavior

TTL (Time To Live) is used to automatically remove stale data.

### Sessions TTL
- Set when the lobby session is created
- Typically, ~30 minutes
- Ensures abandoned sessions don’t accumulate

### Games TTL
- Set when the game is initialized via GameService
- Typically, 1 hour
- Ensures completed games eventually expire
- Cleanup is handled automatically by DynamoDB

**Important:**  
TTL does **not** delete immediately — deletion is asynchronous and can take minutes to hours.  
This is expected behavior.

---

# GSI Behavior

A Global Secondary Index (GSI) named **`SessionIndex`** exists on the Games table:


### Why it exists:

- Lambda needs to look up games by sessionId
- GameService needs to find the correct game for joins
- Prevents unnecessary table scans
- Allows fast, scalable lookup

### Pattern:


This supports O(1) lookup even with many games in the table.

---

# In-Memory Storage
*(GameService active state)*

The GameService keeps active games in a `ConcurrentHashMap<String, Game>`.

### Why in-memory?

Real-time gameplay requires:

- Instant reaction handling
- 50ms tick loop
- Frequent cell updates
- Fast score merging
- Low latency WebSocket broadcasting

DynamoDB is not suitable for:

- Millisecond updates
- High-frequency writes
- Real-time state mutations

### Lifecycle

**Created:**  
When gameService.startGame() is triggered after joinDeadline.

**Maintained:**  
For the full game duration (~35 seconds):

- GameLoopRunner updates cell state
- ReactionHandler updates scores
- GameBroadcaster sends WebSocket events

**Removed:**  
After game completes:

- Final scores saved to DynamoDB
- Top scores saved
- `activeGames.remove(gameId)`
- Game cleaned up

This hybrid architecture (in-memory + DynamoDB) provides both:

- **Fast gameplay**
- **Durable persistence**

---

# Summary

The data layer of QuickR is built for **performance** and **scalability**:

- **Sessions table:** lightweight session validation
- **Games table:** persistent game metadata & scores (with GSI + TTL)
- **TopScores table:** global leaderboard
- **In-memory state:** ultra-fast updates during active gameplay

Each component plays a specific role to support a full real-time multiplayer experience.

