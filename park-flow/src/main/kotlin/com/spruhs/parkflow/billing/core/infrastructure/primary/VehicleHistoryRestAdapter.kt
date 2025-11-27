package com.spruhs.parkflow.billing.core.infrastructure.primary

import com.spruhs.parkflow.billing.core.application.VehicleHistoryQueryPort
import com.spruhs.parkflow.billing.core.domain.HistoryItem
import com.spruhs.parkflow.billing.core.domain.HistoryType
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
    ) = queryPort.findByPlateNumber(PlateNumber(plateNumber)).toMessage()
}

data class VehicleHistoryMessage(
    val plateNumber: String,
    val customerId: String,
    val history: List<HistoryItemMessage>,
)

data class HistoryItemMessage(
    val time: String,
    val type: HistoryType,
    val parkingSpotId: String?,
    val hasDisabilityCard: Boolean?,
    val amount: String?,
)

private fun VehicleHistoryReflection.toMessage() =
    VehicleHistoryMessage(
        plateNumber = this.plateNumber.value,
        customerId = this.customerId,
        history = this.history.map { it.toMessage() },
    )

private fun HistoryItem.toMessage() =
    HistoryItemMessage(
        time = this.time.toString(),
        type = this.type,
        parkingSpotId = this.parkingSpotId,
        hasDisabilityCard = this.hasDisabilityCard,
        amount = this.amount,
    )
