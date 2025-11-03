# 🗓️ Project Work Log

## Week 1 – Project Kickoff & Setup
### Monday 14 October

- Made [Project plan](https://github.com/salt-community/pp-amanda/blob/main/ProjectPlan.md)
- Presenting plan for PGP group

### Tuesday 15 October

#### Scaffolding
- Spring Boot microservice Lobby
- Frontend in Vue3
#### Configured
- application.yml
- application-dev.yml
- application-prod.yml
- Dockerfile in frontend
- nginx.conf in frontend
- Dockerfile in lobby
- Docker-compose.yml in root

#### _sandbox
- Trying out WebSockets

#### AWS/Deploying
- Got started with AWS see [documentation](https://github.com/salt-community/pp-amanda/blob/main/AWSDocumentation.md)

### Wednesday 16 October

- Deployed the app
- Documentation (Deployment flow and Work Log)
- Getting started with Lobby service and connection with Frontend
- Adding SLF4J logging

### Thursday 17 October

- Continuing with connection
- Refresh Vue3 knowledge and create first component + call to backend
- Impl. TanStack Query/Vue
- Setting up Github Action workflows and ECS task definitions
- Impl. HealthController for AWS 

### Friday 18 October

- Continue to get Lobby-service to work as expected in AWS ECS
- Work-progress meeting with PGP group

## Week 2 – Core Development
### Monday 20 October

- Implementing DynamoDB and tried it out locally
- Extending Lobby Controller with join endpoint
- Adding exception handling
- Expanding frontend with pop-ups and redirect both for create and join components
- Releasing new deployed version where a table ``Sessions`` are saved in a DynamoDB

### Tuesday 21 October

- Tasks outside this project.

### Wednesday 22 October

- Tasks outside this project.

### Thursday 23 October

- Writing unit tests and some updates in Lobby-service
- Initialize Game-service with all necessary config
- Try out lobby + game service locally with postman+dynamoDB

### Friday 24 October

- Setup for AWS and with goal to deploy Game-Service

## Week 3 – Feature Expansion & Testing
### Monday 27 October

- Implemented DynamoDB
- Config (cors, AWS-settings etc.) to be able to -->
- Deployed working version of Game-service in AWS 

### Tuesday 28 October

- Working with flow for initiate Game in FE/Vue3
- Implemented SQS + Lambda resulting in re-doing/thinking how Game service will work.
Lambda now consumes and initiates the Game item
- Config to try everything out locally as well as in the cloud

### Wednesday 29 October

- Adjust game domain logic after implementing Lambda
- Working on expanding Game-controller for join-flow (before actual game)
- Align FE to be at the same point as BE to slice the work + context focus

### Thursday 30 October

- Struggling and de-bugging my SQS + Lambda - Now working ✅
- Resulting in both updates in code and in AWS Console

### Friday 31 October

- Planning game-logic and with that some re-structuring in current game-service

## Week 4 – Final Touches & Presentation
### Monday 3 November

- Cleaning up in Game-Service related to DynamoDB
- Start impl. WebSockets and get a grasp of how it will integrate in FE+BE

### Tuesday 4 November (Presentation Day)
