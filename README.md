## Initial Planning

### Project Concept

*Aims to build an application that handles:*
 - **Many interactions**
 - **Real-time events**

*Designed to be:*
 - **Deployed** early and continuously

*Consist of:*
- At least **2 microservices** communicating through **Apache Kafka**
- Using **WebSockets** for live updates
- Frontend built with **Vue3**

### Tech Stack

- **Backend:** Java, Spring Boot
- **Frontend:** Vue3
- **Database:** PostgresSQL
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
        Make a plan                         :a0, 2025-10-13, 1d
    section Setup
        Repo                                :a1, 2025-10-13, 2d
        Frontend Scaffold                   :a2, 2025-10-14, 1d
        WebSocket try out+impl.             :a3, 2025-10-14, 1d
        Connect FE + BE                     :a4, 2025-10-14, 1d
        
        section Lobby Service
        Init Lobby-Service                  :a5, 2025-10-14, 2d
        Create/Join Logic                   :a6, 2025-10-15, 2d

    section CI/CD
        Platform Investigation             :a7, 2025-10-14, 2d
        Initial CI/CD Pipeline Setup       :a8, 2025-10-15, 1d
        Cloud Platform Setup               :a8, 2025-10-16, 2d
        Deploy                             :milestone, 2025-10-17, 0d
```
```mermaid
gantt
    title Week 2 – Core features
    dateFormat  YYYY-MM-DD
    axisFormat  %d %b
    tickInterval 1day

    section Services
        Init Game-Service                   :b1, 2025-10-20, 1d
        Impl. Game logic                    :b2, 2025-10-20, 5d

    section Kafka
        Kafka integration in micro-services     :b3, 2025-10-21, 3d

    section CI/CD
        Continuous Deploy & Manual Testing  :b4, 2025-10-20, 5d

```
```mermaid
gantt
    title Week 3 – Expand and improve
    dateFormat  YYYY-MM-DD
    axisFormat  %d %b
    tickInterval 1day

    section Game Logic
        Expand Core Logic                           :c1, 2025-10-27, 3d

    section Frontend
        Polish Styling                              :c3, 2025-10-29, 3d

    section CI/CD
        Continuous Deploy & Manual Testing          :c5, 2025-10-27, 5d
```

```mermaid
gantt
    title Week 4 – Final Touches & Presentation Prep
    dateFormat  YYYY-MM-DD
    axisFormat  %d %b
    tickInterval 1day
        
    section CI/CD
        Final Deployment                            :d3, 2025-11-03, 1d

    section Presentation
        Reflection, Documentation and Presentation  :d2, 2025-11-03, 2d
        Final Presentation                          :milestone, 2025-11-05, 0d
```
