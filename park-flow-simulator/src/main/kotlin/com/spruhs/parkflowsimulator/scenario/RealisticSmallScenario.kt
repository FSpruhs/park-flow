package com.spruhs.parkflowsimulator.scenario

import com.spruhs.parkflowsimulator.publisher.VehicleEventPublisher
import com.spruhs.parkflowsimulator.webclient.ParkFlowWebClientService
import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["simulation.scenario"],
    havingValue = "realistic-small",
    matchIfMissing = false
)
class RealisticSmallScenario(
    webclient: ParkFlowWebClientService,
    vehicleEventPublisher: VehicleEventPublisher
) : Scenario(webclient, vehicleEventPublisher) {

    private val gates = listOf(
        GateInfo("G1", "ENTRANCE"),
        GateInfo("G2", "EXIT"),
    )

    override suspend fun start() {

        runActions(
            listOf(
                *gates.map { ScenarioAction.CreateGate(it) }.toTypedArray(),
            )
        )

        repeat(100) { i ->
            val types = mutableListOf<String>()
            var price: String? = null
            if (i % 10 == 0) {
                types.add("ELECTRIC")
            }
            if (i % 25 == 0) {
                types.add("DISABLED")
            }
            if (i % 33 == 0) {
                types.add("RENTABLE")
                price = "$i.10"
            }
            runAction(ScenarioAction.CreateParkingSpot(ParkingSpotInfo("P-$i", types, price)))
        }

        createGateQueue("1-${gates[0].id()}")
        createGateQueue("2-${gates[0].id()}")
        createGateQueue("3-${gates[0].id()}")
        repeat(150) { i ->
            var plateNumber = "K-A$i"
            val hasDisabilityCard = i % 33 == 0
            if (i % 10 == 0) {
                plateNumber += "E"
            }
            runAction(ScenarioAction.CreateCustomer(CustomerInfo(plateNumber, hasDisabilityCard = hasDisabilityCard)))
            val queueName = when {
                i < 50 -> "1-${gates[0].id()}"
                i in 50..< 100 -> "2-${gates[0].id()}"
                else -> "3-${gates[0].id()}"
            }
            enqueue(
                queueName,
                VehicleSimulation(
                    plateNumber,
                    gates[0],
                    gates[1],
                    queueName,
                    randomDelayMinuets(),
                    randomDelayMinuets(10.0, 20.0),
                    randomDelayMinuets(),
                    hasDisabilityCard
                )
            )
        }

        closeQueue("1-${gates[0].id()}")
        closeQueue("2-${gates[0].id()}")
        closeQueue("3-${gates[0].id()}")

        startProcessing(
            name = "1-${gates[0].id()}",
            intervalProvider = { (8_000L..12_000L).random() },
            handler = { processVehicle(it) }
        )

        delay(600_000L)

        startProcessing(
            name = "2-${gates[0].id()}",
            intervalProvider = { (8_000L..12_000L).random() },
            handler = { processVehicle(it) }
        )

        delay(600_000L)

        startProcessing(
            name = "3-${gates[0].id()}",
            intervalProvider = { (8_000L..12_000L).random() },
            handler = { processVehicle(it) }
        )

        joinAllJobs()
    }

}
