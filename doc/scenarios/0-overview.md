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

# Available Scenarios

| #                                  | Scenario ID              | Description                                                    | Duration  |
|------------------------------------|--------------------------|----------------------------------------------------------------|-----------|
| [1](1-simple-parking-inventory/1-simple-parking-inventory.md) | simple-parking-inventory | Covers all use cases for creating a parking inventory          | ca. 1m    |
| [2](2-simple-customer-access/2-simple-customer-access.md)   | simple-customer-access   | Covers all use cases for a customer end renting a parking spot | ca. 1m    |
| [3](3-simple-parking-operation/3-simple-parking-operation.md) | simple-parking-operation | Covers use cases for a parking operation                       | ca. 1m    |
| [4](4-realistic-small/4-realistic-small.md)          | realistic-small          | Simulates a small parking lot with realistic timings           | ca. 1h5m  |
| [5](5-realistic-medium/5-realistic-medium.md)         | realistic-medium         | Simulates a medium-sized parking lot with realistic timings    | ca. 3h50m |
| [6](6-realistic-large/6-realistic-large.md)          | realistic-large          | Simulates a large parking lot with realistic timings           | ca. 10h   |

# Realistic Scenario Overview

| Name   | Parking Spots | Gates | Cars   | Entrance Gate Arrival Time | Park On Time | Park Off Time | Parking Time | Total Time |
|--------|---------------|-------|--------|----------------------------|--------------|---------------|--------------|------------|
| small  | 100           | 1     | 150    | 8s - 12s                   | 1m - 2m      | 1m - 2m       | 10m - 20m    | ca. 1h5m   |
| medium | 1.000         | 2     | 1.200  | 8s - 12s                   | 1m - 3m      | 1m - 3m       | 20m - 30m    | ca. 3h50m  |
| large  | 10.000        | 5     | 12.000 | 8s - 12s                   | 1m - 4m      | 1m - 4m       | 20m - 30m    | ca. 10h    |
