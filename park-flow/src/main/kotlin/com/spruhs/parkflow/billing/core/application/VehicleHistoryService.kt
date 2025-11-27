package com.spruhs.parkflow.billing.core.application

import com.spruhs.parkflow.billing.core.domain.HistoryItem
import com.spruhs.parkflow.billing.core.domain.HistoryType
import com.spruhs.parkflow.billing.core.domain.VehicleHistoryReflection
import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.customeraccess.api.CustomerApi
import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleRemovedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.ParkingInventoryApi
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import com.spruhs.parkflow.parkingoperation.api.VehicleEnteredParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleLeavedParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOffEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnWrongParkingSpotEvent
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

@Service
class VehicleHistoryService(
    private val repository: VehicleHistoryRepositoryPort,
    private val paymentPort: PaymentPort,
    private val customerApi: CustomerApi,
    private val parkingInventoryApi: ParkingInventoryApi,
) {
    private val log = getLogger(javaClass)

    private val priceList =
        mutableMapOf(
            PriceListEntry.PARKING_PER_HOUR to BigDecimal("10"),
            PriceListEntry.UNAUTHORIZED_PARKING_ON_DISABLED_SPOT to BigDecimal("100"),
            PriceListEntry.UNAUTHORIZED_PARKING_ON_RENTED_SPOT to BigDecimal("50"),
            PriceListEntry.UNAUTHORIZED_PARKING_ON_ELECTRICAL_SPOT to BigDecimal("80"),
            PriceListEntry.PARKING_ON_WRONG_SPOT to BigDecimal("10"),
        )

    private suspend inline fun handle(
        plateNumber: PlateNumber,
        block: (VehicleHistoryReflection) -> VehicleHistoryReflection,
    ) {
        loadVehicleHistory(plateNumber).also { customer ->
            block(customer).also { repository.save(it) }
        }
    }

    private suspend fun loadVehicleHistory(plateNumber: PlateNumber): VehicleHistoryReflection =
        repository.findByPlateNumber(plateNumber)
            ?: throw IllegalArgumentException("Customer with id ${plateNumber.value} not found")

    suspend fun handleCarParkedOn(event: VehicleParkedOnEvent) {
        handle(event.plateNumber) {
            it.copy(
                history =
                    it.history +
                        HistoryItem(
                            event.time,
                            HistoryType.PARKED_ON_CORRECT,
                            parkingSpotId = event.parkingSpotId.value,
                        ),
            )
        }
    }

    suspend fun handleCarParkedOff(event: VehicleParkedOffEvent) {
        handle(event.plateNumber) {
            it.copy(
                history =
                    it.history +
                        HistoryItem(
                            event.time,
                            HistoryType.PARKED_OFF,
                            parkingSpotId = event.parkingSpotId.value,
                        ),
            )
        }
    }

    suspend fun handleVehicleParkedOnWrongSpot(event: VehicleParkedOnWrongParkingSpotEvent) {
        handle(event.vehicle.plateNumber) {
            it.copy(
                history =
                    it.history +
                        HistoryItem(
                            event.time,
                            HistoryType.PARKED_ON_CORRECT,
                            parkingSpotId = event.parkingSpot.parkingSpotId.value,
                        ),
            )
        }
    }

    suspend fun handleCustomerCreated(event: CustomerCreatedEvent) {
        VehicleHistoryReflection(
            event.plateNumber,
            event.aggregateId,
            listOf(HistoryItem(Instant.now(), HistoryType.CREATED)),
        )
            .also { repository.save(it) }
    }

    suspend fun handleVehicleAdded(event: CustomerVehicleAddedEvent) {
        VehicleHistoryReflection(
            event.plateNumber,
            event.aggregateId,
            listOf(HistoryItem(Instant.now(), HistoryType.CREATED)),
        )
            .also { repository.save(it) }
    }

    suspend fun handleVehicleRemoved(event: CustomerVehicleRemovedEvent) {
        handle(event.plateNumber) {
            it.copy(history = it.history + HistoryItem(Instant.now(), HistoryType.REMOVED))
        }
    }

    suspend fun findByPlateNumber(plateNumber: PlateNumber): VehicleHistoryReflection {
        return loadVehicleHistory(plateNumber)
    }

    suspend fun handleVehicleLeaved(event: VehicleLeavedParkingLotEvent) {
        val history = loadVehicleHistory(event.plateNumber)
        val invoice = createInvoice(history, event)
        paymentPort.charge(invoice)
        handle(event.plateNumber) {
            it.copy(
                history =
                    it.history + HistoryItem(event.time, HistoryType.EXIT) +
                        HistoryItem(
                            Instant.now(),
                            HistoryType.INVOICED,
                            amount = invoice.sumOf { it.second }.toString(),
                        ),
            )
        }
    }

    suspend fun handleVehicleEntered(event: VehicleEnteredParkingLotEvent) {
        handle(event.plateNumber) {
            it.copy(history = it.history + HistoryItem(event.time, HistoryType.ENTER))
        }
    }

    private suspend fun createInvoice(
        history: VehicleHistoryReflection,
        event: VehicleLeavedParkingLotEvent,
    ): List<Pair<String, BigDecimal>> {
        val sortedHistory = history.history.sortedBy { it.time }

        val lastEnterIndex = sortedHistory.indexOfLast { it.type == HistoryType.ENTER }

        if (lastEnterIndex == -1) {
            log.error("No ENTER event found for vehicle ${history.plateNumber.value}, skipping invoice creation")
            return mutableListOf()
        }

        val invoiceEntry: MutableList<Pair<String, BigDecimal>> = mutableListOf()

        var tempHistoryItem: HistoryItem? = null
        for (i in lastEnterIndex until sortedHistory.size) {
            if (sortedHistory[i].type == HistoryType.PARKED_ON_CORRECT) {
                if (!isParkingSpotRented(sortedHistory[i].parkingSpotId ?: "", history.plateNumber)) {
                    val parkingPrice =
                        priceList[PriceListEntry.PARKING_PER_HOUR]?.times(
                            Duration.between(
                                sortedHistory[lastEnterIndex].time,
                                event.time,
                            ).toHours().toBigDecimal(),
                        ) ?: BigDecimal.ZERO
                    invoiceEntry.add("Parking Spot Rent" to parkingPrice)
                } else {
                    invoiceEntry.add("Parking Spot Rent" to BigDecimal.ZERO)
                }
            }

            if (sortedHistory[i].type == HistoryType.PARKED_ON_WRONG) {
                tempHistoryItem = sortedHistory[i]
            }
            if (sortedHistory[i].type == HistoryType.PARKED_OFF) {
                if (tempHistoryItem != null) {
                    val between = Duration.between(tempHistoryItem.time, sortedHistory[i].time).toMinutes()
                    if (between > 5) {
                        val types =
                            tempHistoryItem.parkingSpotId?.let { fetchTypes(it) }
                                ?: emptyList()
                        if (types.contains(ParkingSpotType.Electric) && history.plateNumber.isElectrical()) {
                            invoiceEntry.add(
                                "Unauthorized parking on electrical spot" to (
                                    priceList[PriceListEntry.UNAUTHORIZED_PARKING_ON_ELECTRICAL_SPOT]
                                        ?: BigDecimal.ZERO
                                ),
                            )
                        }
                        if (types.contains(ParkingSpotType.Rentable)) {
                            invoiceEntry.add(
                                "Unauthorized parking on rented spot" to (
                                    priceList[PriceListEntry.UNAUTHORIZED_PARKING_ON_RENTED_SPOT]
                                        ?: BigDecimal.ZERO
                                ),
                            )
                        }
                        if (types.contains(ParkingSpotType.Disabled) && sortedHistory[lastEnterIndex].hasDisabilityCard ?: false) {
                            invoiceEntry.add(
                                "Unauthorized parking on disabled spot" to (
                                    priceList[PriceListEntry.UNAUTHORIZED_PARKING_ON_DISABLED_SPOT]
                                        ?: BigDecimal.ZERO
                                ),
                            )
                        }
                        invoiceEntry.add(
                            "Parked on wrong spot" to (
                                priceList[PriceListEntry.PARKING_ON_WRONG_SPOT]
                                    ?: BigDecimal.ZERO
                            ),
                        )
                    }
                }
                tempHistoryItem = null
            }
        }
        return invoiceEntry.toList()
    }

    private suspend fun isParkingSpotRented(
        parkingSpotId: String,
        plateNumber: PlateNumber,
    ): Boolean {
        return customerApi.isParkingSpotRented(ParkingSpotId(parkingSpotId), plateNumber)
    }

    private suspend fun fetchTypes(parkingSpotId: String): List<ParkingSpotType> {
        return parkingInventoryApi.getParkingSpotTypes(ParkingSpotId(parkingSpotId))
    }
}

enum class PriceListEntry {
    PARKING_PER_HOUR,
    UNAUTHORIZED_PARKING_ON_DISABLED_SPOT,
    UNAUTHORIZED_PARKING_ON_RENTED_SPOT,
    UNAUTHORIZED_PARKING_ON_ELECTRICAL_SPOT,
    PARKING_ON_WRONG_SPOT,
}

fun interface PaymentPort {
    suspend fun charge(invoice: List<Pair<String, BigDecimal>>)
}

interface VehicleHistoryRepositoryPort {
    suspend fun save(vehicleHistory: VehicleHistoryReflection)

    suspend fun findByPlateNumber(plateNumber: PlateNumber): VehicleHistoryReflection?
}
