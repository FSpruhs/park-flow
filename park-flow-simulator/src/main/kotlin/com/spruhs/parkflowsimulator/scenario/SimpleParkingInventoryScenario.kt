package com.spruhs.parkflowsimulator.scenario

import com.spruhs.parkflowsimulator.webclient.ParkflowWebClientService
import kotlinx.coroutines.coroutineScope
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = ["simulation.scenario"],
    havingValue = "simple-parking-inventory",
    matchIfMissing = false
)
class SimpleParkingInventoryScenario(webClient: ParkflowWebClientService) :
    Scenario(webClient) {

    private val parkingSpots = listOf(
        ParkingSpotInfo("A-1"),
        ParkingSpotInfo("A-2", listOf("DISABLED")),
        ParkingSpotInfo("B-1", listOf("RENTABLE", "ELECTRIC"), "10.05"),
        ParkingSpotInfo("B-2"),
        ParkingSpotInfo("C-1")
    )

    private val gates = listOf(
        GateInfo("G1", "ENTRANCE"),
        GateInfo("G2", "EXIT"),
        GateInfo("G3", "EXIT"),
    )

    override suspend fun start(): Unit = coroutineScope {

        runActions(
            listOf(
                *parkingSpots.map { ScenarioAction.CreateParkingSpot(it) }.toTypedArray(),
                *gates.map { ScenarioAction.CreateGate(it) }.toTypedArray(),

                ScenarioAction.ExpectError(400) { webClient.createParkingSpot(parkingSpots[0]) },
                ScenarioAction.ExpectError(400) { createGate(gates[0]) },

                ScenarioAction.Custom { deactivateGate(gates[0]) },
                ScenarioAction.Custom { deactivateGate(gates[1]) },
                ScenarioAction.Custom { removeGate(gates[2]) },
                ScenarioAction.Custom { activateGate(gates[0]) },

                ScenarioAction.ExpectError(404) { webClient.activateGate("wrong gate id") },
                ScenarioAction.ExpectError(400) { activateGate(gates[2]) },

                ScenarioAction.Custom { renameParkingSpot(parkingSpots[0], "D-1") },
                ScenarioAction.Custom { addParkingSpotType(parkingSpots[0], listOf("RENTABLE", "ELECTRIC"), "11.05") },
                ScenarioAction.Custom { removeParkingSpotType(parkingSpots[2], listOf("ELECTRIC")) },
                ScenarioAction.Custom { addParkingSpotType(parkingSpots[1], listOf("ELECTRIC")) },

                ScenarioAction.ExpectError(400) { addParkingSpotType(parkingSpots[1], listOf("RENTABLE"), "12.10") },
                ScenarioAction.ExpectError(404) {
                    webClient.addParkingSpotType(
                        "wrong id",
                        listOf("RENTABLE"),
                        "12.10"
                    )
                },
                ScenarioAction.ExpectError(400) { addParkingSpotType(parkingSpots[3], listOf("RENTABLE")) },

                ScenarioAction.Custom { deactivateParkingSpot(parkingSpots[0]) },
                ScenarioAction.Custom { deactivateParkingSpot(parkingSpots[1]) },
                ScenarioAction.Custom { activateParkingSpot(parkingSpots[0]) },
                ScenarioAction.Custom { removeParkingSpot(parkingSpots[4]) },
                ScenarioAction.ExpectError(400) { deactivateParkingSpot(parkingSpots[4]) },
            )
        )
        log.info("------ Scenario ended ------")
        log.info("------ Start validating scenario ------")

        val parkingInventory = webClient.getParkingInventory()

        if (parkingInventory.gates.count() != 2) error("gate count should be 2 and is actual ${parkingInventory.gates.count()}")
        if (parkingInventory.parkingSpots.count() != 4) error("parking spot count should be 4 and is actual ${parkingInventory.parkingSpots.count()}")

        val parkingValidators = listOf(
            ParkingSpotValidator(parkingSpots[0].id(), "D-1", "ACTIVE", "11.05", setOf("RENTABLE", "ELECTRIC")),
            ParkingSpotValidator(
                parkingSpots[1].id(),
                parkingSpots[1].name,
                "INACTIVE",
                null,
                setOf("DISABLED", "ELECTRIC")
            ),
            ParkingSpotValidator(parkingSpots[2].id(), parkingSpots[2].name, "ACTIVE", "10.05", setOf("RENTABLE")),
            ParkingSpotValidator(parkingSpots[3].id(), parkingSpots[3].name, "ACTIVE", null, emptySet())
        )

        val gateValidators = listOf(
            GateValidator(gates[0].id(), gates[0].name, "ENTRANCE", "ACTIVE"),
            GateValidator(gates[1].id(), gates[1].name, "EXIT", "INACTIVE")
        )

        validateAll(parkingInventory.parkingSpots, parkingValidators) { it.id }
        validateAll(parkingInventory.gates, gateValidators) { it.id }

        log.info("------ Scenario validated correct ------")
    }
}
