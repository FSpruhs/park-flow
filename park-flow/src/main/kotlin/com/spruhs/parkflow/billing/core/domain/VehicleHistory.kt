package com.spruhs.parkflow.billing.core.domain

import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import java.time.Instant
import kotlin.collections.plus

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

fun VehicleHistoryReflection.parkOn(
    time: Instant,
    parkingSpotId: ParkingSpotId,
): VehicleHistoryReflection = addHistoryItem(time, HistoryType.PARKED_ON_CORRECT, parkingSpotId = parkingSpotId)

fun VehicleHistoryReflection.parkOnIncorrect(
    time: Instant,
    parkingSpotId: ParkingSpotId,
): VehicleHistoryReflection = addHistoryItem(time, HistoryType.PARKED_ON_CORRECT, parkingSpotId = parkingSpotId)

fun VehicleHistoryReflection.parkOff(
    time: Instant,
    parkingSpotId: ParkingSpotId,
): VehicleHistoryReflection = addHistoryItem(time, HistoryType.PARKED_OFF, parkingSpotId = parkingSpotId)

fun VehicleHistoryReflection.removeVehicle() = addHistoryItem(Instant.now(), HistoryType.REMOVED)

fun VehicleHistoryReflection.vehicleEntered(
    time: Instant,
    hasDisabilityCard: Boolean,
) = addHistoryItem(time, HistoryType.ENTER, hasDisabilityCard = hasDisabilityCard)

fun VehicleHistoryReflection.vehicleLeaved(time: Instant) = addHistoryItem(time, HistoryType.EXIT)

fun VehicleHistoryReflection.chargeInvoice(invoice: Invoice) =
    addHistoryItem(Instant.now(), HistoryType.INVOICED, amount = invoice.totalAmount.toString())

private fun VehicleHistoryReflection.addHistoryItem(
    time: Instant,
    type: HistoryType,
    parkingSpotId: ParkingSpotId? = null,
    amount: String? = null,
    hasDisabilityCard: Boolean? = null,
): VehicleHistoryReflection =
    copy(
        history =
            history +
                HistoryItem(
                    time = time,
                    type = type,
                    parkingSpotId = parkingSpotId?.value,
                    amount = amount,
                    hasDisabilityCard = hasDisabilityCard,
                ),
    )
