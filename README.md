# Quick-R

*"A cloud-based multiplayer reaction game — built end-to-end with real production concepts."*

## Introduction

As part of the post graduate program at SALT this project represent a project of my own choice.

My main goal was to create something that could mirror how a real production system is structured.

This included:

1) **DevOps & CI/CD** deploying multiple services independently
2) **WebSockets** powering the real-time gameplay
3) **Message Queuing** (SQS → Lambda) — event-driven game initialization
   **BONUS** would be an interactive demo were those who wanted could try it out.

### **Why and How?**

I wanted to build something as close to a real production as possible, a complete own product
using a tech-stack as close as possible as one of a recruiting process I have been a part of.

But how would I package this tech-stack into something that could bring something concrete?


Doing this project the game itself has never been the main part but nevertheless a crucial part since it
embodies the functionality and visualize the flow of all the services behind it.

I wanted to build something complete. A system touching all areas I enjoy and wanted to explore:

* backend architecture 
* AWS cloud services 
* event-driven flows 
* containerized microservices 
* frontend with real-time UI

The game itself never was the “main” goal, but it is the visualization layer that lets you experience all the
underlying systems working together.

### **Tech Stack**

Backend : Java + Spring Boot
Infrastructure : AWS (ECS Fargate, ALB, SQS, Lambda, DynamoDB, ECR)
Frontend : Type Script + Vue3 + TailwindCSS
Local Dev: Docker, LocalStack

## Services

🔗 [Services](https://github.com/salt-community/pp-amanda/blob/main/documentation/services.md)

## Data Layer

🔗 [Data-Layer](https://github.com/salt-community/pp-amanda/blob/main/documentation/data-layer.md)

## Frontend

🔗 [Frontend](https://github.com/salt-community/pp-amanda/blob/main/documentation/frontend.md)

## Infrastructure/DevOps

🔗 [Infrastructure](https://github.com/salt-community/pp-amanda/blob/main/documentation/infrastructure.md)

## Local Development and Deployment

🔗 [Locally](https://github.com/salt-community/pp-amanda/blob/main/documentation/run-local.md)

## Overview and System Flow

*Coming soon: Combined architecture diagram & full flow description.*

## 💭 Reflections and Challenges  

This project introduced a lot of new concepts for me:
AWS services, 
real infrastructure,
event-driven flows, and WebSockets — and much of the work revolved around
configuration, setup and understanding how these pieces connect. 
Not everything is second nature yet, but I have “touched” each part at least once and now have a solid foundation to
return to.

One early challenge was realizing that frequent deployments to AWS wasn’t an option because of cost.
This pushed me to build a full LocalStack environment to mock SQS, Lambda, and DynamoDB. 
Getting this working required much more effort than expected especially because Lambda is serverless and dynamic, 
and SQS needed a stable internal network and the correct environment setup to always find the handler without network errors.

But once the local environment was stable, the whole system became predictable and fun to work with.
Seeing configuration, infrastructure and application logic all come together has been the most rewarding part of the project.

In the end, this wasn’t just about building a game — it was about understanding how real systems behave, 
how distributed components communicate, and how to manage all the small pieces that make cloud software work.
