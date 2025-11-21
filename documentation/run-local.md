# Run Quick-R Locally (with LocalStack)

This project includes a complete local setup so you can run all microservices, the database layer and the Lambda with full AWS-like behavior — but **without using AWS credits**.

Local development uses:

- Spring Boot (Lobby + Game)
- Vue 3 (Frontend)
- LocalStack (DynamoDB, SQS, Lambda)
- Docker Desktop
- Node.js 20+
- AWS CLI (for local DynamoDB/SQS debugging)

---

# 1. Start Backend Services (Lobby + Game)

Open **two terminals**:

### Terminal 1 — Lobby
```bash
cd ./lobby
mvn clean install
mvn spring-boot:run
```


# Try out Quick-R locally using LocalStack

Eventually I will no longer be able to host this on AWS and therefore I early on
decided to have a local set-up as well as I have been using it during development
phase (Once I got it to work...)

And so can you... clone this repo...

And make sure you have docker desktop installed, node version 20 and AWS CLI??? :)

Open a terminal and just split it in 4 squares..

Step into `pp-amanda/lobby` respective `pp-amanda/game`

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

FOr the frontend
```bash
cd ./frontend
npm install
npm run dev
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


NOW ether just run a post from Postman or similar

Download and import this collection: JSON

Or do it in the real frontend

Init a session and ask someone to join (hard on local network but if you know how, do it - check your you computer IP
and share it for someone else to replace localhost with the serial-nr)

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

