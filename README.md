## Initial Planning

### Project Concept

*Aims to build an application that handles:*
 - **Many interactions**
 - **Real-time events**

*Designed to be:*
 - **Deployed** early and continuously

*Consist of:*
- At least **2 micro-services** communicating through **Apache Kafka**
- Using **WebSockets** for live updates
- Frontend build with **Vue3**

### Tech Stack

- **Backend:** Java, Spring Boot
- **Frontend:** Vue3
- **Database:** PostgreSQL
- **Messaging:** Kafka
- **Live Communication:** WebSockets
- **Infra:** Docker
- **Deployment:** TBD

### Why?

My main focus is exploring **event-driven architecture** and **real-time communication.**
I want to challenge myself with focusing on **deployment**, building something **scalable** and easy to **maintain**. But at the same time keep it fun to build and demonstrate.

New areas to (re)discover:

- WebSocket broadcasting/live updates
- Cloud/Deployment
- Vue3

### Goals

- Demonstrate real-time multiplayer experience and event-driven design
- Deploy early and iterate continuously
- Keep frontend lightweight - let backend own all possible logic
- Have a working, deployed and polished demo for final presentation.

```mermaid
gantt
    title Week 1 – Setup
    dateFormat  YYYY-MM-DD
    axisFormat  %d %b
    tickInterval 1day

    section Planning
        Make a plan                       :a0, 2025-10-13, 1d
    section Setup
        Repo              :a1, 2025-10-13, 2d
        Frontend Scaffold                :a2, 2025-10-14, 1d
        WebSocket try out+impl.           :a3, 2025-10-14, 1d
        Connect FE + BE                   :a4, 2025-10-14, 1d
        
        section Lobby Service
            
            Init Lobby-Service           :a5, 2025-10-14, 2d
    Create/Join Logic     :a6, 2025-10-15, 2d

    section CI/CD
        Platform Investigation             :a7, 2025-10-14, 2d
        Initial CI/CD Pipeline Setup       :a8, 2025-10-15, 1d
        Cloud Platform Setup               :a8, 2025-10-16, 2d
        Deploy   :milestone, 2025-10-17, 0d
```