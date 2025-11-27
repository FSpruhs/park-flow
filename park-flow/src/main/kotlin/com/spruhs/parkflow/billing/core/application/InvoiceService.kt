package com.spruhs.parkflow.billing.core.application

import com.spruhs.parkflow.billing.core.domain.FeePosition
import com.spruhs.parkflow.billing.core.domain.HistoryItem
import com.spruhs.parkflow.billing.core.domain.HistoryType
import com.spruhs.parkflow.billing.core.domain.Invoice
import com.spruhs.parkflow.billing.core.domain.VehicleHistoryReflection
import com.spruhs.parkflow.billing.core.domain.addExtraCharges
import com.spruhs.parkflow.billing.core.domain.addParkingPerHour
import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.customeraccess.api.CustomerApi
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.ParkingInventoryApi
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import com.spruhs.parkflow.parkingoperation.api.VehicleLeavedParkingLotEvent
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant

@Component
class InvoiceService(
    private val paymentPort: PaymentPort,
    private val customerApi: CustomerApi,
    private val parkingInventoryApi: ParkingInventoryApi,
) {
    private val log = getLogger(javaClass)

    suspend fun invoice(
        history: VehicleHistoryReflection,
        event: VehicleLeavedParkingLotEvent,
    ): Invoice {
        val sortedHistory = history.history.sortedBy { it.time }
        val lastEnterIndex = sortedHistory.indexOfLast { it.type == HistoryType.ENTER }

        if (lastEnterIndex == -1) {
            log.error("No ENTER event found for vehicle ${history.plateNumber.value}, skipping invoice creation")
            throw IllegalStateException("No ENTER event found for vehicle ${history.plateNumber.value}")
        }

        return createInvoice(
            sortedHistory,
            lastEnterIndex,
            history,
            event.time,
        ).also { paymentPort.charge(it) }
    }

    private suspend fun createInvoice(
        sortedHistory: List<HistoryItem>,
        lastEnterIndex: Int,
        history: VehicleHistoryReflection,
        leaveTime: Instant,
    ): Invoice {
        val invoice = Invoice()

        var tempHistoryItem: HistoryItem? = null
        val enterTime = sortedHistory[lastEnterIndex].time
        val hasDisabilityCard = sortedHistory[lastEnterIndex].hasDisabilityCard ?: false

        sortedHistory.subList(lastEnterIndex, sortedHistory.size).forEach { actualItem ->

            parkingTimeCharge(
                parkingSpotId = actualItem.parkingSpotId ?: "",
                plateNumber = history.plateNumber,
                enterTime = enterTime,
                leaveTime = leaveTime,
            ).also { invoice.addParkingPerHour(it) }

            if (actualItem.type == HistoryType.PARKED_ON_WRONG) {
                tempHistoryItem = actualItem
            }

            if (actualItem.type == HistoryType.PARKED_OFF) {
                tempHistoryItem?.let { temp ->
                    val minutesBetween = Duration.between(temp.time, actualItem.time).toMinutes()
                    if (minutesBetween > 5) {
                        extraCharges(temp, isElectrical(history), hasDisabilityCard)
                            .also { invoice.addExtraCharges(it) }
                    }
                }
                tempHistoryItem = null
            }
        }

        return invoice
    }

    private suspend fun isElectrical(history: VehicleHistoryReflection) = history.plateNumber.isElectrical()

    private suspend fun isParkingSpotRented(
        parkingSpotId: String,
        plateNumber: PlateNumber,
    ) = customerApi.isParkingSpotRented(ParkingSpotId(parkingSpotId), plateNumber)

    private suspend fun parkingTimeCharge(
        parkingSpotId: String,
        plateNumber: PlateNumber,
        enterTime: Instant,
        leaveTime: Instant,
    ): FeePosition.ParkingPerHour {
        if (isParkingSpotRented(parkingSpotId, plateNumber)) {
            return FeePosition.ParkingPerHour(Duration.ZERO)
        }

        return FeePosition.ParkingPerHour(Duration.between(enterTime, leaveTime))
    }

    private suspend fun extraCharges(
        historyItem: HistoryItem,
        isElectrical: Boolean,
        hasDisabilityCard: Boolean,
    ): List<FeePosition> {
        val types = historyItem.parkingSpotId?.let { fetchTypes(it) } ?: emptyList()

        return listOfNotNull(
            if (ParkingSpotType.Electric in types && !isElectrical) {
                FeePosition.UnauthorizedParkingOnElectricSpot
            } else {
                null
            },
            if (ParkingSpotType.Rentable in types) {
                FeePosition.UnauthorizedParkingOnRentedSpot
            } else {
                null
            },
            if (ParkingSpotType.Disabled in types && hasDisabilityCard) {
                FeePosition.UnauthorizedParkingOnDisabledSpot
            } else {
                null
            },
            FeePosition.ParkingOnWrongSpot,
        )
    }

    private suspend fun fetchTypes(parkingSpotId: String) =
        parkingInventoryApi.getParkingSpotTypes(ParkingSpotId(parkingSpotId))
}

fun interface PaymentPort {
    suspend fun charge(invoice: Invoice)
}
