# Park Flow Simulator Scenarios

⚠️ **WARNING:** Each scenario run will first delete **all contents of the database**. Make sure you do not need any
existing data before running a simulation.

The simulator can be executed via Gradle and requires a **Scenario ID**:

```bash
./gradlew -p park-flow-simulator bootRun --args='--simulation.scenario=<SCENARIO_ID>'
```

After starting the simulator with a specific Scenario ID, it will automatically execute all defined steps from that
scenario against the running Park Flow application and validate the result.

✅ After a successful simulation, the application will automatically shut down.

## Available Scenarios

| #                                                             | Scenario ID              | Description                                                    | Duration  |
|---------------------------------------------------------------|--------------------------|----------------------------------------------------------------|-----------|
| [1](1-simple-parking-inventory/1-simple-parking-inventory.md) | simple-parking-inventory | Covers all use cases for creating a parking inventory          | ca. 1m    |
| [2](2-simple-customer-access/2-simple-customer-access.md)     | simple-customer-access   | Covers all use cases for a customer end renting a parking spot | ca. 1m    |
| [3](3-simple-parking-operation/3-simple-parking-operation.md) | simple-parking-operation | Covers use cases for a parking operation                       | ca. 1m    |
| [4](4-realistic-small/4-realistic-small.md)                   | realistic-small          | Simulates a small parking lot with realistic timings           | ca. 1h5m  |
| [5](5-realistic-medium/5-realistic-medium.md)                 | realistic-medium         | Simulates a medium-sized parking lot with realistic timings    | ca. 2h45m |
| [6](6-realistic-large/6-realistic-large.md)                   | realistic-large          | Simulates a large parking lot with realistic timings           | ca. 10h   |

## Realistic Scenario Overview

| Name   | Parking Spots | Gates | Vehicles | Entrance Gate Arrival Time | Park On Time | Park Off Time | Parking Time | Total Time |
|--------|---------------|-------|----------|----------------------------|--------------|---------------|--------------|------------|
| small  | 100           | 1     | 150      | 8s - 12s                   | 1m - 2m      | 1m - 2m       | 10m - 20m    | ca. 1h5m   |
| medium | 1.000         | 2     | 1.200    | 8s - 12s                   | 1m - 3m      | 1m - 3m       | 20m - 30m    | ca. 2h45m  |
| large  | 10.000        | 5     | 12.000   | 8s - 12s                   | 1m - 4m      | 1m - 4m       | 20m - 30m    | ca. 10h    |

## Realistic Scenario Results

| Category         | Value           | small            | medium           | large | 
|------------------|-----------------|------------------|------------------|-------|
| JVM Memory       |                 |                  |                  |       |
|                  | max. used       | 505 MiB          | 464 MiB          |       |
|                  | median used     | 361 MiB          | 343 MiB          |       |
|                  | mean used       | 367 MiB          | 344 MiB          |       |
|                  | max. commited   | 591 MiB          | 535 MiB          |       |
|                  | median commited | 590 MiB          | 535 MiB          |       |
|                  | mean commited   | 558 MiB          | 535 MiB          |       |
|                  | max. maximal    | 16,6 GiB         | 16,6 GiB         |       |
|                  | median maximal  | 16,6 GiB         | 16,6 GiB         |       |
|                  | mean maximal    | 16,6 GiB         | 16,6 GiB         |       |
| CPU Usage        |                 |                  |                  |       |
|                  | max. system     | 3,5%             | 2,1%             |       |
|                  | median system   | 0,8%             | 0,6%             |       |
|                  | mean system     | 1,0%             | 0,7%             |       |
|                  | max. process    | 1,6%             | 11,1%            |       |
|                  | median process  | 0,0%             | 0,1%             |       |
|                  | mean process    | 0,1%             | 0,2%             |       |
| Threads          |                 |                  |                  |       |
|                  | max. live       | 117              | 120              |       |
|                  | max. peak       | 117              | 120              |       |
|                  | max. deamon     | 74               | 77               |       |
|                  | max. runable    | 58               | 58               |       |
| Database Storage |                 |                  |                  |       |
|                  | Events          | 1.000 kB         | 6.704 kB         |       |
|                  | Snapshots       | 200 kB           | 272 kB           |       |
| Events Published |                 |                  |                  |       |
|                  | Total           | 1.302            | 10.604           |       |
|                  | Max             | 0,8 events/sec   | 1,78 events/sec  |       |
|                  | Median          | 0,333 events/sec | 1,0 events/sec   |       |
|                  | Mean            | 0,349 events/sec | 0,929 events/sec |       |
| Events Consumed  |                 |                  |                  |       |
|                  | Total           | 3.557            | 29.213           |       |
|                  | Max             | 1,89 events/sec  | 4,33 events/sec  |       |
|                  | Median          | 0,8 events/sec   | 2,38 events/sec  |       |
|                  | Mean            | 0,851 events/sec | 2,26 events/sec  |       |
| Events RabbitMQ  |                 |                  |                  |       |
|                  | Total           | 900              | 7.200            |       |
|                  | Max             | 0,689 events/sec | 1,56 events/sec  |       |
|                  | Median          | 0,289 events/sec | 0,822 events/sec |       |
|                  | Mean            | 0,301 events/sec | 0,797 events/sec |       |

