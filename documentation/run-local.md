# Run Quickr Locally (with LocalStack)

This project includes a complete local setup so you can run all microservices, the database layer and the Lambda with full AWS-like behavior — but **without using AWS credits**.

###  Prerequisites

- Docker Desktop (required for LocalStack)
- Node.js 20+ 
- AWS CLI (used by LocalStack scripts)
- Java 21 
- Maven Wrapper (included)

---
## Setup

#### Clone repository!

```bash
git clone https://github.com/salt-community/pp-amanda.git
cd ./pp-amanda
```

You need to add a `.env` file in each service.

#### LOBBY
```bash
cd ./lobby

touch .env

cat << 'EOF' > .env
SPRING_PROFILES_ACTIVE=dev
AWS_REGION=eu-north-1
LOBBY_PORT=8080
FRONTEND_PORT=5173
LOCAL_DEV_IP=192.168.X.XX
EOF
```

#### GAME
```bash
cd ./game

touch .env

cat << 'EOF' > .env
SPRING_PROFILES_ACTIVE=dev
AWS_REGION=eu-north-1
GAME_PORT=8081
FRONTEND_PORT=5173
LOCAL_DEV_IP=192.168.X.XX
EOF
```

#### FRONTEND
```bash
cd ./frontend

touch .env

cat << 'EOF' > .env
VITE_LOBBY_URL=http://localhost:8080
VITE_GAME_URL=http://localhost:8081
EOF
```

>>NOTE:
>>To test on a mobile device, replace LOCAL_DEV_IP=192.168.X.XX with correct
>>LAN IP (e.g. 192.168.x.x).

#### LAMBDA
```bash
cd ./lambda
touch .env

cat << EOF > .env
AWS_REGION=eu-north-1
AWS_ACCESS_KEY_ID=test
AWS_SECRET_ACCESS_KEY=test

LOCALSTACK_HOST=localstack
LOCALSTACK_URL=http://localstack:4566

GAME_TABLE=Games
SESSION_TABLE=Sessions

QUEUE_NAME=session-created-queue

GAME_SERVICE_URL=http://host.docker.internal:8081

ROLE_ARN=arn:aws:iam::000000000000:role/dummy-role

LAMBDA_NAME=session-created-handler
EOF
```

# Get the services running

Open a terminal and make new tabs when needed to run everything!

In each tab
```bash
cd ./lobby
mvn clean install
mvn spring-boot:run

```

```bash
cd ./game
mvn clean install
mvn spring-boot:run

```

For the frontend
```bash
cd ./frontend
npm install
npm run dev -- --host
```

Start your Docker Desktop and anywhere in your terminal
run these command to make sure you docker is cleaned up.
```bash
docker stop localstack && docker rm localstack
docker network rm quickr-net || true
docker system prune -f
rm -rf ~/.localstack .localstack
```
Grant access to run scripts
```bash
cd ./scripts
chmod +x setup-localstack.sh
chmod +x deploy-lambda.sh
```
First run localStack script and wait until it is up and running (logs will help you) (check in docker app it pops up)
```bash
./setup-localstack.sh
```
Then deploy your Lambda and connect it to the SQS
```bash
./deploy-lambda.sh
```


NOW let's play! 

Init a session and ask someone to join. 
>NOTE that you must replace localhost in Frontend `.env` to
correct(your computers) LAN IP (e.g. 192.168.x.x).

Check the logs in each terminal tab to see how the services are communicating/integrating


You can extend with multiple terminal windows to watch how the tables are filled in like::

```bash
watch -n 10 'aws dynamodb scan \
--table-name Sessions \
--endpoint-url http://localhost:8000 \
--query "Items[*].{ID: id.S, Name: name.S}" \
--output table'
```
```bash
watch -n 10 'aws dynamodb scan \
--table-name Games\
--endpoint-url http://localhost:8000 \
--query "Items[*].{ID: id.S, Name: name.S}" \
--output table'
```
```bash
watch -n 10 'aws dynamodb scan \
--table-name TopScores \
--endpoint-url http://localhost:8000 \
--query "Items[*].{Player: playerName.S, Score: score.N}" \
--output table'
```