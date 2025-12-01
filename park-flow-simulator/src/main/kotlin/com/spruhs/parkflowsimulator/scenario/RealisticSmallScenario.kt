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

    private suspend fun createGates() {
        runActions(
            listOf(
                *gates.map { ScenarioAction.CreateGate(it) }.toTypedArray(),
            )
        )
    }

    override suspend fun start() {

        createGates()

        createGenericParkingSpots(100)

        val gateQueueNames = buildGateQueueNames(gates[0], 3)
            .also { gateNames -> gateNames.forEach { createGateQueue(it) } }

        createGenericVehicles(
            150,
        ) {
            when {
                it < 50 -> Triple(gates[0], gates[1], gateQueueNames[0])
                it in 50..<100 -> Triple(gates[0], gates[1], gateQueueNames[1])
                else -> Triple(gates[0], gates[1], gateQueueNames[2])
            }
        }

        gateQueueNames.forEach(::closeQueue)

        repeat(3) { i ->
            startProcessing(
                name = gateQueueNames[i],
                intervalProvider = { (secondsToMillis(8)..secondsToMillis(12)).random() },
                handler = { processVehicle(it) }
            )

            if (i != 2) {
                delay(minutesToMillis(10))
            }
        }

        joinAllJobs()

        log.info("------ Scenario ended ------")
        log.info("------ Start validating scenario ------")

        validateGenericHistory(150)

        log.info("------ Scenario validation correct ------")
    }
}
