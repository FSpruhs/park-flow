package com.spruhs.parkflow.billing.core.adapter.primary

import com.spruhs.parkflow.billing.core.application.VehicleHistoryQueryPort
import com.spruhs.parkflow.billing.core.domain.VehicleHistoryReflection
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/billing/vehicle-history")
class VehicleHistoryRestAdapter(
    private val queryPort: VehicleHistoryQueryPort,
) {
    @GetMapping("/{plateNumber}")
    suspend fun getHistoryByPlate(
        @PathVariable plateNumber: String,
    ): VehicleHistoryReflection {
        return queryPort.findByPlateNumber(PlateNumber(plateNumber))
    }
}
