package com.spruhs.parkflow.billing.core.domain

import com.spruhs.parkflow.common.helper.generateId
import com.spruhs.parkflow.customeraccess.api.CustomerId
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

class Invoice(
    val invoiceId: InvoiceId,
    val customerId: CustomerId,
    val plateNumber: PlateNumber,
    val items: MutableList<InvoiceItem> = mutableListOf(),
    var totalAmount: BigDecimal = BigDecimal.ZERO,
) {
    companion object {
        suspend fun create(
            customerId: CustomerId,
            plateNumber: PlateNumber,
            history: List<HistoryItem>,
            leaveTime: Instant,
            isParkingSpotRented: suspend (String, PlateNumber) -> Boolean,
            fetchTypes: suspend (String) -> List<ParkingSpotType>,
        ): Invoice {
            val sortedHistory = history.sortedBy { it.time }
            val lastEnterIndex = sortedHistory.indexOfLast { it.type == HistoryType.ENTER }

            check(lastEnterIndex != -1) {
                throw IllegalStateException("No ENTER event found for vehicle ${plateNumber.value}")
            }

            val invoice =
                Invoice(
                    invoiceId = InvoiceId(generateId()),
                    customerId = customerId,
                    plateNumber = plateNumber,
                )

            val enterTime = sortedHistory[lastEnterIndex].time
            val hasDisabilityCard = sortedHistory[lastEnterIndex].hasDisabilityCard ?: false
            var chargedPerHour = false
            var tempHistoryItem: HistoryItem? = null

            for (item in sortedHistory.subList(lastEnterIndex, sortedHistory.size)) {
                when (item.type) {
                    HistoryType.PARKED_ON_CORRECT, HistoryType.PARKED_ON_WRONG -> {
                        if (!chargedPerHour) {
                            parkingTimeCharge(
                                parkingSpotId = item.parkingSpotId.orEmpty(),
                                plateNumber = plateNumber,
                                enterTime = enterTime,
                                leaveTime = leaveTime,
                                isParkingSpotRented = isParkingSpotRented,
                            ).also {
                                invoice.totalAmount += it.price
                                invoice.items.add(it.calculateInfoItem())
                            }
                            chargedPerHour = true
                        }

                        if (item.type == HistoryType.PARKED_ON_WRONG) {
                            tempHistoryItem = item
                        }
                    }

                    HistoryType.PARKED_OFF -> {
                        tempHistoryItem?.let { wrongItem ->
                            extraCharges(wrongItem, plateNumber.isElectrical(), hasDisabilityCard, fetchTypes)
                                .forEach {
                                    invoice.totalAmount += it.price
                                    invoice.items.add(it.calculateInfoItem())
                                }
                            tempHistoryItem = null
                        }
                    }

                    else -> Unit
                }
            }

            return invoice
        }

        private suspend fun parkingTimeCharge(
            parkingSpotId: String,
            plateNumber: PlateNumber,
            enterTime: Instant,
            leaveTime: Instant,
            isParkingSpotRented: suspend (String, PlateNumber) -> Boolean,
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
            fetchTypes: suspend (String) -> List<ParkingSpotType>,
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
    }
}

data class InvoiceItem(val amount: BigDecimal, val feePosition: FeePosition)

private fun FeePosition.calculateInfoItem(): InvoiceItem {
    return when (this) {
        FeePosition.ParkingOnWrongSpot -> InvoiceItem(this.price, this)
        is FeePosition.ParkingPerHour -> {
            val result =
                if (duration == Duration.ZERO) {
                    BigDecimal.ZERO
                } else {
                    this.price * maxOf(1, duration.toHours()).toBigDecimal()
                }
            InvoiceItem(result, this)
        }
        FeePosition.UnauthorizedParkingOnDisabledSpot -> InvoiceItem(this.price, this)
        FeePosition.UnauthorizedParkingOnElectricSpot -> InvoiceItem(this.price, this)
        FeePosition.UnauthorizedParkingOnRentedSpot -> InvoiceItem(this.price, this)
    }
}

sealed class FeePosition(open val price: BigDecimal) {
    object ParkingOnWrongSpot : FeePosition(BigDecimal("10"))

    object UnauthorizedParkingOnDisabledSpot : FeePosition(BigDecimal("100"))

    object UnauthorizedParkingOnRentedSpot : FeePosition(BigDecimal("80"))

    object UnauthorizedParkingOnElectricSpot : FeePosition(BigDecimal("50"))

    data class ParkingPerHour(val duration: Duration) : FeePosition(BigDecimal("10"))

    fun name() =
        when (this) {
            is ParkingPerHour -> "Parking per hour"
            ParkingOnWrongSpot -> "Parking on wrong spot"
            UnauthorizedParkingOnDisabledSpot -> "Unauthorized parking on disabled spot"
            UnauthorizedParkingOnElectricSpot -> "Unauthorized parking on electric spot"
            UnauthorizedParkingOnRentedSpot -> "Unauthorized parking on rented spot"
        }
}

@JvmInline
value class InvoiceId(val value: String) {
    init {
        require(value.isNotBlank()) { "Identifier cannot be blank" }
    }
}
