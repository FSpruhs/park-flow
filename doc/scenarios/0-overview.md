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

| #                                  | Scenario ID              | Description                                                    | Duration |
|------------------------------------|--------------------------|----------------------------------------------------------------|----------|
| [1](1-simple-parking-inventory.md) | simple-parking-inventory | Covers all use cases for creating a parking inventory          | 1 min    |
| [2](2-simple-customer-access.md)   | simple-customer-access   | Covers all use cases for a customer end renting a parking spot | 1 min    |
| [3](3-simple-parking-operation.md) | simple-parking-operation | Covers use cases for a parking operation                       | 1 min    |
