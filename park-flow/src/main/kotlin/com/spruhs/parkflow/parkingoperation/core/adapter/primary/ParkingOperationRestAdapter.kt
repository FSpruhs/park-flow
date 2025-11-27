package com.spruhs.parkflow.parkingoperation.core.adapter.primary

import com.spruhs.parkflow.parkingoperation.core.application.ParkingOperationCommandPort
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/parking-operation")
class ParkingOperationRestAdapter(private val commandPort: ParkingOperationCommandPort) {
    @DeleteMapping("/cache")
    suspend fun clearCache() {
        commandPort.clearCache()
    }
}
