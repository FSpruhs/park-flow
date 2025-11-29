package com.spruhs.parkflowsimulator.scenario

import com.spruhs.parkflowsimulator.publisher.VehicleEventPublisher
import com.spruhs.parkflowsimulator.webclient.ParkFlowWebClientService
import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["simulation.scenario"],
    havingValue = "simple-parking-operation",
    matchIfMissing = false
)
class SimpleParkingOperationScenario(
    webclient: ParkFlowWebClientService,
    vehicleEventPublisher: VehicleEventPublisher
) : Scenario(webclient, vehicleEventPublisher) {

    private val parkingSpots = listOf(
        ParkingSpotInfo("P-1"),
        ParkingSpotInfo("P-2", listOf("ELECTRIC")),
        ParkingSpotInfo("P-3", listOf("DISABLED")),
        ParkingSpotInfo("P-4", listOf("RENTABLE"), "20.06"),
        ParkingSpotInfo("P-5", listOf("RENTABLE", "ELECTRIC"), "21.06"),
        ParkingSpotInfo("P-6", listOf("RENTABLE"), "22.06"),
    )

    private val gates = listOf(
        GateInfo("G1", "ENTRANCE"),
        GateInfo("G2", "EXIT"),
    )

    private val customers = listOf(
        CustomerInfo("K-A1E"),
        CustomerInfo("K-A2", hasDisabilityCard = true),
        CustomerInfo("K-A3H"),
        CustomerInfo("K-B1E"),
        CustomerInfo("K-B2"),
        CustomerInfo("K-B3"),
        CustomerInfo("K-B4"),
        CustomerInfo("K-B5"),
        CustomerInfo("K-B6"),
    )

    private val actionList = listOf(
        *parkingSpots.map { ScenarioAction.CreateParkingSpot(it) }.toTypedArray(),
        *gates.map { ScenarioAction.CreateGate(it) }.toTypedArray(),
        *customers.map { ScenarioAction.CreateCustomer(it) }.toTypedArray(),
        ScenarioAction.Custom { rentParkingSpot(customers[8], parkingSpots[5], customers[8].plateNumber) },
    )

    private suspend fun createGateQueues() {
        createGateQueue("1-${gates[0].id()}")
        createGateQueue("2-${gates[0].id()}")
        customers.forEachIndexed { index, customer ->
            when (index) {
                in 0..5 -> {
                    enqueue(
                        "1-${gates[0].id()}",
                        VehicleSimulation(customer.plateNumber, gates[0], gates[1], "1-${gates[0].id()}", 500, 500, 100, customer.hasDisabilityCard)
                    )
                }
                6 -> enqueue("2-${gates[0].id()}", VehicleSimulation(customer.plateNumber, gates[0], gates[1], "2-${gates[0].id()}", 10000, 1000, 100, customer.hasDisabilityCard))
                7 -> enqueue("2-${gates[0].id()}", VehicleSimulation(customer.plateNumber, gates[0], gates[1], "2-${gates[0].id()}", 1000, 1000, 100, customer.hasDisabilityCard, parkingSpots[0]))
                8 -> enqueue("2-${gates[0].id()}", VehicleSimulation(customer.plateNumber, gates[0], gates[1], "2-${gates[0].id()}", 1000, 1000, 100, customer.hasDisabilityCard))
            }
        }
        closeQueue("1-${gates[0].id()}")
        closeQueue("2-${gates[0].id()}")
    }

    override suspend fun start() {
        runActions(actionList)

        createGateQueues()

        log.info("------ Start gate queue ------")

        startProcessing(
            name = "1-${gates[0].id()}",
            intervalProvider = { (100..200L).random() },
            handler = { processVehicle(it) }
        )
        delay(3000)

        startProcessing(
            name = "2-${gates[0].id()}",
            intervalProvider = { (100..200L).random() },
            handler = { processVehicle(it) }
        )

        joinAllJobs()

        log.info("------ Scenario ended ------")
        log.info("------ Start validating scenario ------")

        validateScenario()

        log.info("------ Scenario validated correct ------")
    }

    private suspend fun validateScenario() {
        val histories = customers.map { getVehicleHistory(it.plateNumber) }

        if (histories.count() != 9) error("vehicle history count should be 9 and is actual ${histories.count()}")

        val historyValidators = listOf(
            VehicleHistoryValidator(
                customers[0].plateNumber,
                6,
                mapOf(
                    HistoryType.CREATED to 1,
                    HistoryType.ENTER to 1,
                    HistoryType.PARKED_ON_CORRECT to 1,
                    HistoryType.PARKED_OFF to 1,
                    HistoryType.EXIT to 1
                ),
                parkingSpots[1].id(),
                "10"
            ),
            VehicleHistoryValidator(
                customers[1].plateNumber,
                6,
                mapOf(
                    HistoryType.CREATED to 1,
                    HistoryType.ENTER to 1,
                    HistoryType.PARKED_ON_CORRECT to 1,
                    HistoryType.PARKED_OFF to 1,
                    HistoryType.EXIT to 1,
                    HistoryType.INVOICED to 1
                ),
                parkingSpots[2].id(),
                "10"
            ),
            VehicleHistoryValidator(
                customers[2].plateNumber,
                6,
                mapOf(
                    HistoryType.CREATED to 1,
                    HistoryType.ENTER to 1,
                    HistoryType.PARKED_ON_CORRECT to 1,
                    HistoryType.PARKED_OFF to 1,
                    HistoryType.EXIT to 1,
                    HistoryType.INVOICED to 1
                ),
                parkingSpots[0].id(),
                "10"
            ),
            VehicleHistoryValidator(
                customers[3].plateNumber,
                6,
                mapOf(
                    HistoryType.CREATED to 1,
                    HistoryType.ENTER to 1,
                    HistoryType.PARKED_ON_CORRECT to 1,
                    HistoryType.PARKED_OFF to 1,
                    HistoryType.EXIT to 1,
                    HistoryType.INVOICED to 1
                ),
                parkingSpots[4].id(),
                "10"
            ),
            VehicleHistoryValidator(
                customers[4].plateNumber,
                6,
                mapOf(
                    HistoryType.CREATED to 1,
                    HistoryType.ENTER to 1,
                    HistoryType.PARKED_ON_CORRECT to 1,
                    HistoryType.PARKED_OFF to 1,
                    HistoryType.EXIT to 1,
                    HistoryType.INVOICED to 1
                ),
                parkingSpots[3].id(),
                "10"
            ),
            VehicleHistoryValidator(
                customers[5].plateNumber,
                1,
                mapOf(
                    HistoryType.CREATED to 1
                )
            ),
            VehicleHistoryValidator(
                customers[6].plateNumber,
                6,
                mapOf(
                    HistoryType.CREATED to 1,
                    HistoryType.ENTER to 1,
                    HistoryType.PARKED_ON_CORRECT to 1,
                    HistoryType.PARKED_OFF to 1,
                    HistoryType.EXIT to 1,
                    HistoryType.INVOICED to 1
                ),
                parkingSpots[3].id(),
                "10"
            ),
            VehicleHistoryValidator(
                customers[7].plateNumber,
                6,
                mapOf(
                    HistoryType.CREATED to 1,
                    HistoryType.ENTER to 1,
                    HistoryType.PARKED_ON_WRONG to 1,
                    HistoryType.PARKED_OFF to 1,
                    HistoryType.EXIT to 1,
                    HistoryType.INVOICED to 1
                ),
                parkingSpots[0].id(),
                expectedPrice = "20"
            ),
            VehicleHistoryValidator(
                customers[8].plateNumber,
                6,
                mapOf(
                    HistoryType.CREATED to 1,
                    HistoryType.ENTER to 1,
                    HistoryType.PARKED_ON_CORRECT to 1,
                    HistoryType.PARKED_OFF to 1,
                    HistoryType.EXIT to 1,
                    HistoryType.INVOICED to 1
                ),
                parkingSpots[5].id(),
                expectedPrice = "0"
            ),
        )
        validateAll(histories, historyValidators) { it.plateNumber }
    }
}
