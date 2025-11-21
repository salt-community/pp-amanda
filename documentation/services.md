# Lobby

*"Create and verifies game sessions"*

The Lobby Service is the project/systems entry point of the (QuickR) system

#### Handles/Responsible for:

* Generating a session and validate existing sessions
* Allowing players to join
* Creating DynamoDB entries
* Emitting/publishing an event to SQS

The Lobby microservice only ensures tha a session exists and is valid
before the player proceeds.

### SQS - a part of Lobby service


```
[ LobbyController ]
     |
     | 1. Create session
     v
[ SessionService ]
     |
     | 2. Save to DynamoDB
     v
[ SessionRepository ]
     |
     | 3. Publish SessionCreatedEvent
     v
[ SqsPublisher ]
     |
     | 4. sendMessage()
     v
[ SQS Queue: session-created-queue ]

```



# AWS Lambda - **session-created-handler**

*"Consumes the Lobby event and initialize a Game entry"*

This Lambda function is the bridge between the Lobby service and
the Game service.
When Lobby publishes a `session.created.v1` event to SQS, 
this Lambda consumes the message and performs three tasks:

1. Prevent duplicate games
* The Lambda checks DynamoDB to see if a game already
exists for the given sessionId using the GSI SessionIndex:
If a game exists → the Lambda logs and skips processing.

2. Create a new Game entry
* If no existing game is found, the Lambda:
* Generates a gameId using randomUUID()
* Stores the new entry in the Games DynamoDB table 
* It includes a safe ConditionExpression in production
to prevent race conditions.

3. Call the Game service to initialize the game
Once DynamoDB entry is created, the Lambda makes an HTTP POST request to the Game service:

This triggers:
* join window setup
* game initialization 
* countdown/broadcast logic

(I.e., the Lambda does not start the game — the Game service handles all logic.)

#### Lambda Flow

```
        SQS Event
            |
            v
   [ Lambda Handler ]
            |
            | 1. Check if game exists
            v
   DynamoDB (Games Table)
            |
            | 2. Create game if missing
            v
   DynamoDB (PutItem)
            |
            | 3. Notify Game Service
            v
  POST {GAME_BASE_URL}/game/init

```

### Lambda Function

The Lambda is written in JavaScript, and the execution environment in both production and local development is Node.js.
The only difference is where it runs:

* **Production** AWS Lambda manages the Node.js runtime for you
* **Local development** You run the Lambda manually through scripts (Node + LocalStack)

#### Serverless Behavior

Serverless means you don't manage the server.
AWS automatically handles provisioning, scaling, and shutting down the compute environment.

`Lobby → SQS → Lambda → Game-service`

This means:

* SQS triggers the Lambda 
* Lambda starts only when invoked 
* LocalStack simulates this by spinning up a temporary Lambda runtime
* It shuts down again immediately after execution

### Difference between Prod and Local

*AWS handles all internal networking for you in production.
LocalStack does not.*

LOCAL:
* It loads .env 
* Uses **LocalStack** endpoints (LocalStack for DynamoDB + SQS)
* Calls Game-service using `host.docker.internal` 
* It supports both TS and JS 
* You manually run it with the scripts I have provided
* 
**To run locally**

🔗 [Locally](https://github.com/salt-community/pp-amanda/blob/main/documentation/run-local.md)

**View scripts**

🔗 [LocalStack Setup](https://github.com/salt-community/pp-amanda/blob/main/scripts/setup-localstack.sh)

🔗 [Deploy Lambda](https://github.com/salt-community/pp-amanda/blob/main/scripts/deploy-lambda.sh)

AWS Lambda in production/deployed

* Runs in AWS Lambda runtime (Node.js 20)
* Has no .env — uses Lambda configuration / environment variables in AWS
* Uses real AWS endpoints (DynamoDB, SQS) 
* Does NOT need endpointOverride 
* Reaches Game-service through public ALB URL 
* Must be uploaded as **JavaScript** (TS → compile → JS)

### Key learnings:

To make SQS consistently find the Lambda in your local setup:

* Put LocalStack inside a dedicated Docker network (quickr-net)
* Ensure all services (SQS, Lambda, DynamoDB) share the same network 
* Pass correct AWS_REGION, endpoint overrides, and environment variables 
* Re-deploy the Lambda every time the code changes

Without this, SQS can’t reach the Lambda container → connection errors.

## Game-Service

**"Handles player joining, game initialization,
real-time reaction gameplay, scoring & top list"**

The Game-service is the core service of the QuickR system.
It is responsible for:

* Creating the game entry (triggered asynchronously by Lambda)
* Managing players joining 
* Enforcing join deadlines 
* Running the reaction-based game loop 
* Broadcasting game events over WebSockets 
* Processing reactions in real-time 
* Saving results and top scores 
* Applying TTL and using DynamoDB GSI for fast lookups

This service is intentionally stateful during
an active game, using in-memory storage for live performance, 
falling back to DynamoDB for persistence.

Game-service-flow (end-to-end)

#### 1 Game Initialization (POST /game/init)

Triggered by the Lambda after Lobby publishes an event.

1. Lambda calls /game/init

2. Game-service:

* Looks for Game by sessionId and check it is not already initialized

Sets:

* type = REACTION 
* joinDeadline = now + 60s 
* Time To Live = now + 1 hour (DynamoDB TTL)

Saves game to DynamoDB

* Starts a background scheduler thread:
* Sleeps until joinDeadline 
* If none players joins → cancel game
* Else → start game loop

#### 2 Player Join (POST /game/{sessionId}/join)

Frontend calls this when a user writes their name and joins.

Game-service:

1. Finds game by sessionId (using GSI)

2. Checks joinDeadline - If expired → throw `DeadlinePassedException`

3. Updates DynamoDB:

* Ensures players-map exists 
* Adds the player with initial score 0 

4. Returns updated GameResponse

This guarantees DynamoDB always has full game state.

#### 3 Countdown & Game Start

When joinDeadline passes, the scheduled thread:

1. Loads the latest Game

2. If no players → logs & cancels

3. Else:

* Broadcasts countdown (10 sec)
* Submits the game loop to an ExecutorService

This gives non-blocking, scalable task handling.

#### 4. Real-time Reaction Game Loop (GameLoopRunner)

The Loop:
* Runs for approx. 35 seconds
* Manages active "cells"
* Every 50ms it removes and might (likability percentage) spawns new cells
and broadcast current cells and live scores to FE.

`GameLoopRunner.java` 
uses
* Supplier<Game> to fetch current state 
* Consumer<Game> to update in-memory state 
* Runnable onFinish to persist final players to DynamoDB + TopScores

Communication via WebSockets:
```bash

/topic/game/{gameId}
/topic/game/{gameId}/countdown
/topic/game/{gameId}/scores
/topic/game/{gameId}/over

```

```
                  ┌──────────────────────────────┐
                  │        Lambda Trigger         │
                  │   calls /game/init (POST)     │
                  └───────────────┬──────────────┘
                                  │
                                  v
                         ┌──────────────────┐
                         │ InitGameService  │
                         └───────┬──────────┘
                 Sets joinDeadline | ttl | type
                                  │
                                  v
                         ┌──────────────────┐
                         │   GameRepository │─── DynamoDB (Games)
                         └───────┬──────────┘
                                  │
     ┌────────────────────────┐   │   ┌────────────────────────────┐
     │ Player joins (HTTP)    │   │   │ startGame()                │
     │ POST /game/{sId}/join  │   │   │ - broadcast countdown      │
     └─────────────┬──────────┘   │   │ - run game loop (thread)  │
                   v              │   └────────────────────────────┘
             ┌──────────────┐     │
             │ addPlayer()  │ <───┘
             └─────┬────────┘
                   │
                   v
             In-memory Game State
              (ConcurrentHashMap)

                   │ 50ms tick
                   v
       ┌────────────────────────────┐
       │ GameLoopRunner             │
       │ - spawn cells              │
       │ - remove expired           │
       │ - broadcast round          │
       └───────────┬────────────────┘
                   │
                   v
             WebSocket Broadcasts

```