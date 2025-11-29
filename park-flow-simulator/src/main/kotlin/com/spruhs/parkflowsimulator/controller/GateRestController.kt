package com.spruhs.parkflowsimulator.controller

import com.spruhs.parkflowsimulator.scenario.Scenario
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/gate")
class GateRestController(private val scenario: Scenario) {
    @PostMapping("/{gateId}/entrance/{plateNumber}/{parkingSpotId}")
    suspend fun openEntranceGate(
        @PathVariable gateId: String,
        @PathVariable plateNumber: String,
        @PathVariable parkingSpotId: String
    ) {
        scenario.openEntranceGate(plateNumber, parkingSpotId)
    }

    @PostMapping("/{gateId}/exit/{plateNumber}")
    suspend fun openExitGate(
        @PathVariable gateId: String,
        @PathVariable plateNumber: String,
    ) {
        scenario.openExitGate(plateNumber)
    }

    @PostMapping("/{gateId}/error/{plateNumber}")
    suspend fun errorGate(
        @PathVariable gateId: String,
        @PathVariable plateNumber: String,
    ) {
        scenario.errorGate(plateNumber)
    }
}
