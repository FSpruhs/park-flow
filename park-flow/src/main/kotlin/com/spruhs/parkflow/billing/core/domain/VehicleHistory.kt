package com.spruhs.parkflow.billing.core.domain

import com.spruhs.parkflow.customeraccess.api.PlateNumber
import java.time.Instant

data class VehicleHistoryReflection(
    val plateNumber: PlateNumber,
    val customerId: String,
    val history: List<HistoryItem>,
)

data class HistoryItem(
    val time: Instant,
    val type: HistoryType,
    val parkingSpotId: String? = null,
    val hasDisabilityCard: Boolean? = null,
    val amount: String? = null,
)

enum class HistoryType {
    CREATED,
    ENTER,
    PARKED_ON_CORRECT,
    PARKED_ON_WRONG,
    PARKED_OFF,
    EXIT,
    REMOVED,
    INVOICED,
    PAYED,
}
