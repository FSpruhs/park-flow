package com.spruhs.parkflowsimulator.controller

import com.spruhs.parkflowsimulator.scenario.Scenario
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notification")
class NotificationRestController(private val scenario: Scenario) {

    @PostMapping("/reprovide/{plateNumber}/{parkingSpotId}")
    suspend fun reprovideNotification(
        @PathVariable plateNumber: String,
        @PathVariable parkingSpotId: String
    ) {
        scenario.notifyReprovideParkingSpot(plateNumber, parkingSpotId)
    }
}
