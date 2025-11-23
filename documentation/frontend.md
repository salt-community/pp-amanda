# Frontend

The QuickR frontend is a **Vue 3 + TypeScript** application built with the Composition API, Vite, and TailwindCSS.  
Its primary purpose is to serve a fast, reactive, real-time UI for the multiplayer reaction game.

The structure focuses on clarity, separation of concerns, and predictable data flow.
Barrel export pattern (index.ts) makes importing clean and are used in this project.
---


---

# Vue Architecture

The frontend is built using the **Composition API**, which means that:

- **Views** contain layout and page-specific UI
- **Components** contain reusable visual elements
- **Composables** contain the logic and can be reused everywhere

This clearly separates **UI** from **logic**, making the flow predictable.

---

## Views

Views represent pages in the app:

- `LobbyView`
- `AboutView`
- `JoinView`
- `GameView`
- `TopListView`

Views do not contain business logic.  
Instead, they use composables and components to fetch data or handle interactivity.

## Components

Components contain the UI pieces and in Vue are the built up as SFC and
stands for Singel File Component which means it can include all 3 of 
html template, JS/TS script and styling(CSS):

* Buttons 
* Forms 
* Game board 
* Scoreboard 
* Random name generator 
* Layout elements (header/footer)

They are visual building blocks and stay as simple as possible.


## Composables
Composable functions encapsulate all business logic:

* fetching or mutating backend data 
* connecting via WebSocket 
* managing reactive game state 
* handling user actions 
* validating join deadlines 
* generating random names

## WebSocker Integration

Real-time gameplay is handled through STOMP over WebSocket.
STOMP = Simple Text Oriented Messaging Protocol
It is an application-level protocol similar/inspired by HTTP.

The composable `useGameSocket()` manages:

* connecting/disconnecting 
* subscribing to topics 
* receiving countdown events 
* receiving active cell updates 
* receiving live score updates 
* publishing reaction events back to the backend

```
                            ┌──────────────────────────────┐
                            │          LobbyView            │
                            │  - CreateSession              │
                            │  - JoinSession                │
                            └──────────────┬───────────────┘
                                           │
                     SessionId →───────────┘
                                           │
                           ┌────────────────────────────┐
                           │          JoinView          │
                           │ - RandomPlayerName         │
                           │ - /game/{sessionId}/join   │
                           └─────────────┬──────────────┘
                                         │  gameId + playerName
                                         v
                            ┌────────────────────────────┐
                            │          GameView           │
                            │  useGameSocket()            │
                            │  - Subscribe topics         │
                            │  - Countdown               │
                            │  - GameBoard               │
                            │  - ScoreBoard              │
                            └─────────────┬──────────────┘
                                          │
                                          v
                               Real-time WebSocket Events
                             (countdown, cells, scores, game over)

```