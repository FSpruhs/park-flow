package com.spruhs.parkflowsimulator.scenario

import com.spruhs.parkflowsimulator.publisher.VehicleEventPublisher
import com.spruhs.parkflowsimulator.webclient.ParkFlowWebClientService
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
    )

    private val actionList = listOf(
        *parkingSpots.map { ScenarioAction.CreateParkingSpot(it) }.toTypedArray(),
        *gates.map { ScenarioAction.CreateGate(it) }.toTypedArray(),
        *customers.map { ScenarioAction.CreateCustomer(it) }.toTypedArray(),
    )

    private suspend fun createQueue() {
        createGateQueue(gates[0].id())
        for (customer in customers) {
            enqueue(
                gates[0].id(),
                VehicleSimulation(customer.plateNumber, gates[0], gates[1], 1000, 6000, 100, customer.hasDisabilityCard)
            )
            log.info("Added customer: ${customer.plateNumber} to queue")
        }
        closeQueue(gates[0].id())
    }

    override suspend fun start() {
        runActions(actionList)

        createQueue()

        log.info("------ Start gate queue ------")

        startProcessing(
            name = gates[0].id(),
            intervalProvider = { (100..500L).random() },
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

        if (histories.count() != 6) error("vehicle history count should be 6 and is actual ${histories.count()}")

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
                parkingSpots[1].id()
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
                parkingSpots[2].id()
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
                )
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
                )
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
                )
            ),
            VehicleHistoryValidator(
                customers[5].plateNumber,
                1,
                mapOf(
                    HistoryType.CREATED to 1
                )
            ),
        )
        validateAll(histories, historyValidators) { it.plateNumber }
    }
}
