package com.spruhs.parkflow.parkingoperation.core.infrastructure.primary

import com.spruhs.parkflow.parkingoperation.core.application.ParkingOperationCommandPort
import com.spruhs.parkflow.parkingoperation.core.application.ParkingOperationQueryPort
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/parking-operation")
class ParkingOperationRestAdapter(
    private val commandPort: ParkingOperationCommandPort,
    private val queryPort: ParkingOperationQueryPort,
) {
    @DeleteMapping("/cache")
    suspend fun clearCache() {
        commandPort.clearCache()
    }

    @GetMapping("/live-map")
    suspend fun getLiveMap() = queryPort.getLiveMap()
}
