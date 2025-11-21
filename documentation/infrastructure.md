# Infrastructure & DevOps

The QuickR system is deployed on **AWS ECS Fargate**, backed by:
- Amazon ECR (container registry)
- Application Load Balancers
- DynamoDB tables for persistent storage
- SQS queue for event-driven communication
- Lambda for backend-triggered initialization
- GitHub Actions for automated CI/CD
- IAM roles for secure access control

Infrastructure is built with a "microservice-first" mindset where each part is independently deployable.

---

# 1. ECS / Fargate Setup

Each of the three services runs as its own **ECS Service**:

| Service   | Port | Purpose                                  |
|-----------|------|-------------------------------------------|
| Lobby     | 8080 | Create/join sessions + publish SQS event  |
| Game      | 8081 | Game logic, WebSockets, scoring           |
| Frontend  | 80   | Vue3 SPA served via Nginx                 |

### Tasks run in Fargate
All services use:
- `awsvpc` network mode
- Auto-managed ENI (Elastic Network Interfaces)
- Connected to public subnets
- ALBs route traffic to each service

---

# 2. Task Definitions

Each service defines:
- CPU & memory
- Container image
- Ports
- Logging (awslogs)
- IAM role associations
- Env variables (region, profiles, URLs)

### Example (Lobby task):

```json
{
  "family": "lobby-task",
  "networkMode": "awsvpc",
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::xxx:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::xxx:role/LobbyTaskRole",
  "containerDefinitions": [
    {
      "name": "lobby",
      "image": "xxx.dkr.ecr.eu-north-1.amazonaws.com/lobby:TAG",
      "portMappings": [{ "containerPort": 8080 }],
      "environment": [
        { "name": "SPRING_PROFILES_ACTIVE", "value": "prod" },
        { "name": "AWS_REGION", "value": "eu-north-1" }
      ]
    }
  ]
}
```

# 3. Dockerfiles

Each backend service uses a multi-stage Maven → JDK build
Frontend

The frontend builds using Vite and is served through Nginx.
Build args inject environment variables

# 4. GitHub Actions Workflows

Each service has its own CI/CD pipeline all containing these steps:

- Checkout code 
- Configure AWS credentials 
- Login to ECR 
- Build Docker image 
- Push to ECR 
- Render task definition 
- Deploy new task to ECS 
- Wait for service stability

# 5. Environment Configs

### Backend (application.yml + .env)

Two profiles:
* dev (LocalStack, local ports)
* prod (real DynamoDB/SQS + AWS region)

Important keys:
* `aws.region`
* `aws.dynamodb.endpoint`
* `aws.sqs.endpoint`

### Frontend (.env) - built at deploy-time

### Lambda (.env) - local only

# 6. IAM Roles & Permissions

**ecsTaskExecutionRole**

Used by Fargate to:

* Pull images from ECR 
* Write logs to CloudWatch

**GameTaskRole**

Allows:
* DynamoDB read/write 
* WebSocket messaging (if needed)

**LobbyTaskRole**

Allows:
* DynamoDB read/write 
* SQS SendMessage

**Lambda execution role**

Allows:
* Read DynamoDB 
* Write DynamoDB 
* Poll SQS 
* Invoke GameService

Roles are strictly scoped to least-privilege.