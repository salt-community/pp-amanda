# Deployment Flow

## Local environment using profiles

By defining different profiles in application.yml referring to respective environment.

**Run locally**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
**Run in docker**
```bash
ENV SPRING_PROFILES_ACTIVE=prod
```

## AWS

Been using both the AWS CLI together with the web interface.

Installing and configure AWS CLI - TBD
Setting region
Connect to your AWS accountn(access_key_id and secret_access_key)

### ECR - Elastic Container Registry

Create **ECR Repository(s)** used to be able to build, tag(latest or other version) and push image(s) to.
```bash
aws ecr create-repository --repository-name *repo-name*
```
**login**
```bash
aws ecr get-login-password --region eu-north-1 | docker login --username AWS --password-stdin <ACCOUNT_ID>.dkr.ecr.eu-north-1.amazonaws.com
```
**build**
```bash
docker build -t *repo-name* ./{path}
```
**give tag**
```bash
docker tag backend:latest <ACCOUNT_ID>.dkr.ecr.eu-north-1.amazonaws.com/backend:latest
```
**push**
```bash
docker push <ACCOUNT_ID>.dkr.ecr.eu-north-1.amazonaws.com/backend:latest
```

### ECS - Elastic Container Service

Create an **ECS Cluster** to orchestrate Docker containers

```bash
aws ecs create-cluster --cluster-name *cluster-name*
```

#### EC2 or VPC dashboard?
Used for Security Groups (sg-0xxx) and Load Balancers + Target Groups
#### Fargate
Serverless containers 

### Define Task Definition

Configure which image, port, environment variables, memory CPU etc)
You can define this task in you project like:
`frontend-task.json`
```json
{
  "family": "frontend-task",
  "networkMode": "awsvpc",
  "containerDefinitions": [
    {
      "name": "frontend",
      "image": "<ACCOUNT_ID>.dkr.ecr.eu-north-1.amazonaws.com/frontend:latest",
      "portMappings": [{ "containerPort": 80, "protocol": "tcp" }],
      "essential": true
    }
  ],
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "executionRoleArn": "arn:aws:iam::<ACCOUNT_ID>:role/ecsTaskExecutionRole"
}
```

and register through AWS CLI:

```bash
aws ecs register-task-definition --cli-input-json file://frontend-task.json
```

### Create a Service

A service starts and keeps the Tasks alive. The Tasks usually are connected to a Target-group in a Load Balancer(ALB).
```bash
aws ecs create-service \
  --cluster *cluster-name* \
  --service-name *service-name* \
  --task-definition *task-name* \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-XXXXXX],securityGroups=[sg-XXXXXX],assignPublicIp=ENABLED}" \
  --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:eu-north-1:...:targetgroup/frontend-tg/...,containerName=frontend,containerPort=80"
```


Things to have in mind:
uses linux amd64
add your SG and sebnets