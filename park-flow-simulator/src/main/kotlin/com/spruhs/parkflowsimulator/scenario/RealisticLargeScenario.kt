package com.spruhs.parkflowsimulator.scenario

import com.spruhs.parkflowsimulator.publisher.VehicleEventPublisher
import com.spruhs.parkflowsimulator.webclient.ParkFlowWebClientService
import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["simulation.scenario"],
    havingValue = "realistic-large",
    matchIfMissing = false
)
class RealisticLargeScenario(
    webclient: ParkFlowWebClientService,
    vehicleEventPublisher: VehicleEventPublisher
): Scenario(webclient, vehicleEventPublisher) {

    private val gates = listOf(
        GateInfo("G1", "ENTRANCE"),
        GateInfo("G2", "ENTRANCE"),
        GateInfo("G3", "ENTRANCE"),
        GateInfo("G4", "ENTRANCE"),
        GateInfo("G5", "ENTRANCE"),
        GateInfo("G6", "EXIT"),
        GateInfo("G7", "EXIT"),
        GateInfo("G8", "EXIT"),
        GateInfo("G9", "EXIT"),
        GateInfo("G10", "EXIT"),
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
        createGenericParkingSpots(10_000)

        val gateQueue1Names = buildGateQueueNames(gates[0], 4)
            .also { gateNames -> gateNames.forEach { createGateQueue(it) } }

        val gateQueue2Names = buildGateQueueNames(gates[1], 4)
            .also { gateNames -> gateNames.forEach { createGateQueue(it) } }

        val gateQueue3Names = buildGateQueueNames(gates[2], 4)
            .also { gateNames -> gateNames.forEach { createGateQueue(it) } }

        val gateQueue4Names = buildGateQueueNames(gates[3], 4)
            .also { gateNames -> gateNames.forEach { createGateQueue(it) } }

        val gateQueue5Names = buildGateQueueNames(gates[4], 4)
            .also { gateNames -> gateNames.forEach { createGateQueue(it) } }

        createGenericVehicles(
            12_000,
            parkOnDelay = 1.0 to 4.0,
            parkOffDelay = 20.0 to 30.0,
            exitDelay = 1.0 to 4.0,
        ) {
            when {
                it < 600 -> Triple(gates[0], gates[5], gateQueue1Names[0])
                it in 600..<1200 -> Triple(gates[1], gates[6], gateQueue2Names[0])
                it in 1200..<1800 -> Triple(gates[2], gates[7], gateQueue3Names[0])
                it in 1800..<2400 -> Triple(gates[3], gates[8], gateQueue4Names[0])
                it in 2400..<3000 -> Triple(gates[4], gates[9], gateQueue5Names[0])
                it in 3000..<3600 -> Triple(gates[0], gates[5], gateQueue1Names[1])
                it in 3600..<4200 -> Triple(gates[1], gates[6], gateQueue2Names[1])
                it in 4200..<4800 -> Triple(gates[2], gates[7], gateQueue3Names[1])
                it in 4800..<5400 -> Triple(gates[3], gates[8], gateQueue4Names[1])
                it in 5400..<6000 -> Triple(gates[4], gates[9], gateQueue5Names[1])
                it in 6000..<6600 -> Triple(gates[0], gates[5], gateQueue1Names[2])
                it in 6600..<7200 -> Triple(gates[1], gates[6], gateQueue2Names[2])
                it in 7200..<7800 -> Triple(gates[2], gates[7], gateQueue3Names[2])
                it in 7800..<8400 -> Triple(gates[3], gates[8], gateQueue4Names[2])
                it in 8400..<9000 -> Triple(gates[4], gates[9], gateQueue5Names[2])
                it in 9000..<9600 -> Triple(gates[0], gates[5], gateQueue1Names[3])
                it in 9600..<10200 -> Triple(gates[1], gates[6], gateQueue2Names[3])
                it in 10200..<10800 -> Triple(gates[2], gates[7], gateQueue3Names[3])
                it in 10800..<11400 -> Triple(gates[3], gates[8], gateQueue4Names[3])
                else -> Triple(gates[4], gates[9], gateQueue5Names[3])
            }
        }

        repeat(3) { i ->
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

            startProcessing(
                name = gateQueue3Names[i],
                intervalProvider = { (secondsToMillis(8)..secondsToMillis(12)).random() },
                handler = { processVehicle(it) }
            )

            startProcessing(
                name = gateQueue4Names[i],
                intervalProvider = { (secondsToMillis(8)..secondsToMillis(12)).random() },
                handler = { processVehicle(it) }
            )

            startProcessing(
                name = gateQueue5Names[i],
                intervalProvider = { (secondsToMillis(8)..secondsToMillis(12)).random() },
                handler = { processVehicle(it) }
            )

            if (i != 2) {
                delay(minutesToMillis(115))
            }
        }

        joinAllJobs()

        log.info("------ Scenario ended ------")
        log.info("------ Start validating scenario ------")

        validateGenericHistory(12_000)

        log.info("------ Scenario validation correct ------")
    }
}
