package com.spruhs.parkflow.billing.core.application

import com.spruhs.parkflow.billing.core.domain.FeePosition
import com.spruhs.parkflow.billing.core.domain.HistoryItem
import com.spruhs.parkflow.billing.core.domain.HistoryType
import com.spruhs.parkflow.billing.core.domain.Invoice
import com.spruhs.parkflow.billing.core.domain.InvoiceId
import com.spruhs.parkflow.billing.core.domain.VehicleHistoryReflection
import com.spruhs.parkflow.billing.core.domain.add
import com.spruhs.parkflow.billing.core.domain.addAll
import com.spruhs.parkflow.common.helper.generateId
import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.customeraccess.api.CustomerApi
import com.spruhs.parkflow.customeraccess.api.CustomerId
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.ParkingInventoryApi
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant

@Component
class InvoiceService(
    private val paymentPort: PaymentPort,
    private val customerApi: CustomerApi,
    private val parkingInventoryApi: ParkingInventoryApi,
    private val repository: InvoiceRepositoryPort,
) {
    private val log = getLogger(javaClass)
    suspend fun getAll() = repository.getAll()

    suspend fun invoice(
        history: VehicleHistoryReflection,
        leaveTime: Instant,
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
            leaveTime,
        ).also {
            paymentPort.charge(it)
            repository.save(it)
        }
    }

    private suspend fun createInvoice(
        sortedHistory: List<HistoryItem>,
        lastEnterIndex: Int,
        history: VehicleHistoryReflection,
        leaveTime: Instant,
    ): Invoice {
        val invoice =
            Invoice(
                InvoiceId(generateId()),
                CustomerId(history.customerId),
                history.plateNumber,
            )

        var chargedPerHour = false
        var tempHistoryItem: HistoryItem? = null
        val enterTime = sortedHistory[lastEnterIndex].time
        val hasDisabilityCard = sortedHistory[lastEnterIndex].hasDisabilityCard ?: false

        sortedHistory.subList(lastEnterIndex, sortedHistory.size).forEach { actualItem ->

            if (actualItem.type == HistoryType.PARKED_ON_CORRECT && !chargedPerHour) {
                parkingTimeCharge(
                    parkingSpotId = actualItem.parkingSpotId ?: "",
                    plateNumber = history.plateNumber,
                    enterTime = enterTime,
                    leaveTime = leaveTime,
                ).also { invoice.add(it) }
                    .also { chargedPerHour = true }
            }

            if (actualItem.type == HistoryType.PARKED_ON_WRONG) {
                if (!chargedPerHour) {
                    parkingTimeCharge(
                        parkingSpotId = actualItem.parkingSpotId ?: "",
                        plateNumber = history.plateNumber,
                        enterTime = enterTime,
                        leaveTime = leaveTime,
                    ).also { invoice.add(it) }
                        .also { chargedPerHour = true }
                }
                tempHistoryItem = actualItem
            }

            if (actualItem.type == HistoryType.PARKED_OFF) {
                tempHistoryItem?.let { tempItem ->
                        extraCharges(tempItem, isElectrical(history), hasDisabilityCard)
                            .also { invoice.addAll(it) }
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

interface InvoiceRepositoryPort {
    suspend fun save(invoice: Invoice)

    suspend fun getAll(): List<Invoice>
}
