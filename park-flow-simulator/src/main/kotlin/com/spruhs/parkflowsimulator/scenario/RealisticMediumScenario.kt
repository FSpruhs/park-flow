package com.spruhs.parkflowsimulator.scenario

import com.spruhs.parkflowsimulator.publisher.VehicleEventPublisher
import com.spruhs.parkflowsimulator.webclient.ParkFlowWebClientService
import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["simulation.scenario"],
    havingValue = "realistic-medium",
    matchIfMissing = false
)
class RealisticMediumScenario(
    webclient: ParkFlowWebClientService,
    vehicleEventPublisher: VehicleEventPublisher
): Scenario(webclient, vehicleEventPublisher) {

    private val gates = listOf(
        GateInfo("G1", "ENTRANCE"),
        GateInfo("G2", "ENTRANCE"),
        GateInfo("G3", "EXIT"),
        GateInfo("G4", "EXIT"),
    )

    private suspend fun createGates() {
        runActions(
            listOf(
                *gates.map { ScenarioAction.CreateGate(it) }.toTypedArray(),
            )
        )
    }

    override suspend fun start() {
        createGates()

        createGenericParkingSpots(1000)

        val gateQueue1Names = buildGateQueueNames(gates[0], 6)
            .also { gateNames -> gateNames.forEach { createGateQueue(it) } }

        val gateQueue2Names = buildGateQueueNames(gates[1], 6)
            .also { gateNames -> gateNames.forEach { createGateQueue(it) } }

        createGenericVehicles(
            1200,
            parkOnDelay = 1.0 to 3.0,
            parkOffDelay = 20.0 to 30.0,
            exitDelay = 1.0 to 3.0,
        ) {
            when {
                it < 100 -> Triple(gates[0], gates[2], gateQueue1Names[0])
                it in 100..<200 -> Triple(gates[1], gates[3], gateQueue2Names[0])
                it in 200..<300 -> Triple(gates[0], gates[2], gateQueue1Names[1])
                it in 300..<400 -> Triple(gates[1], gates[3], gateQueue2Names[1])
                it in 400..<500 -> Triple(gates[0], gates[2], gateQueue1Names[2])
                it in 500..<600 -> Triple(gates[1], gates[3], gateQueue2Names[2])
                it in 600..<700 -> Triple(gates[0], gates[2], gateQueue1Names[3])
                it in 700..<800 -> Triple(gates[1], gates[3], gateQueue2Names[3])
                it in 800..<900 -> Triple(gates[0], gates[2], gateQueue1Names[4])
                it in 900..<1000 -> Triple(gates[1], gates[3], gateQueue2Names[4])
                it in 1000..<1100 -> Triple(gates[0], gates[2], gateQueue1Names[5])
                else -> Triple(gates[1], gates[3], gateQueue2Names[5])
            }
        }

        repeat(6) { i ->
            startProcessing(
                name = gateQueue1Names[i],
                intervalProvider = { (secondsToMillis(8)..secondsToMillis(12)).random() },
                handler = { processVehicle(it) }
            )

            startProcessing(
                name = gateQueue2Names[i],
                intervalProvider = { (secondsToMillis(8)..secondsToMillis(12)).random() },
                handler = { processVehicle(it) }
            )

            if (i != 5) {
                delay(minutesToMillis(20))
            }
        }

        joinAllJobs()

        log.info("------ Scenario ended ------")
        log.info("------ Start validating scenario ------")

        validateGenericHistory(1200)

        log.info("------ Scenario validation correct ------")
    }
}
