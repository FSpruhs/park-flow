# Park Flow

This repository contains a Spring Boot application (Gradle) and a local infrastructure stack provided via Docker Compose.

## Requirements

- Docker & Docker Compose installed
- JDK 21+ installed
- Gradle Wrapper available (already included in the project)

## Repository Structure
```text
/
 |-- docker-compose.yml
 |-- park-flow/                  <- Spring Boot main application root folder (Gradle project)
 |-- park-flow-simulator/        <- Spring Boot test application root folder (Gradle project)
 |-- park-sensor-mock/           <- Lib with sensor mock events (Gradle project)
 |-- doc/                        <- Documentation folder
 |-- settings.gradle.kts         <- Gradle Multi Module Settings
```

## How to run locally

### 1. Start the infrastructure (Docker Compose)

```bash
docker compose up -d --build
```

### 2. Build the Spring Boot application

```bash
./gradlew clean build -x test
```

### 3. Run the Spring Boot application

```bash
cd park-flow
./gradlew bootRun
```

# Park Flow Simulator

In addition to the main application, this repository contains a Spring Boot application called **park-flow-simulator**.
This simulator can automatically execute predefined scenarios against the Park Flow backend and validate the results while running locally.

## Scenarios

Multiple scenario definitions are available inside the `docs/scenarios/` folder.
Each scenario has its own file and contains a **Scenario ID**, which is required to run the scenario via the simulator.

## Run the Simulator
⚠️ **WARNING:** Each scenario run will first delete **all contents of the database**. Make sure you do not need any existing data before running a simulation.

The simulator can be executed via Gradle and requires a scenario parameter:
```bash
cd park-flow-simulator
./gradlew bootRun --args='--simulation.scenario=<SCENARIO_ID>'
```
After starting the simulator with a specific Scenario ID, it will automatically execute all defined steps from that scenario against the running Park Flow application and validate the results.

✅ Note: After a successful simulation and validation, the application will automatically shut down.

## Grafana Dashboard

Grafana is included in the Park Flow Docker Compose stack for monitoring.

### Start Grafana

Start the infrastructure (including Grafana) with:

```bash
docker compose up -d --build
```

Grafana will be available at: [http://localhost:3000](http://localhost:3000)

### Login

- **Username:** admin
- **Password:** admin

### Dashboards

All dashboards in `monitoring/grafana/dashboards` are automatically loaded on startup.  
Just open Grafana and explore the dashboards.




