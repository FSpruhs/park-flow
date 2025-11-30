package com.spruhs.parkflow.billing.core.application

import com.spruhs.parkflow.billing.core.domain.HistoryItem
import com.spruhs.parkflow.billing.core.domain.HistoryType
import com.spruhs.parkflow.billing.core.domain.VehicleHistoryReflection
import com.spruhs.parkflow.billing.core.domain.chargeInvoice
import com.spruhs.parkflow.billing.core.domain.parkOff
import com.spruhs.parkflow.billing.core.domain.parkOn
import com.spruhs.parkflow.billing.core.domain.parkOnIncorrect
import com.spruhs.parkflow.billing.core.domain.removeVehicle
import com.spruhs.parkflow.billing.core.domain.vehicleEntered
import com.spruhs.parkflow.billing.core.domain.vehicleLeaved
import com.spruhs.parkflow.common.helper.KeyedMutex
import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleRemovedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkingoperation.api.VehicleEnteredParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleLeavedParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOffEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnWrongEvent
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class VehicleHistoryService(
    private val repository: VehicleHistoryRepositoryPort,
    private val invoiceService: InvoiceService,
) {
    private val mutex = KeyedMutex<PlateNumber>()

    suspend fun handleCarParkedOn(event: VehicleParkedOnEvent) =
        handle(event.plateNumber) { it.parkOn(event.time, event.parkingSpotId) }

    suspend fun handleCarParkedOff(event: VehicleParkedOffEvent) =
        handle(event.plateNumber) { it.parkOff(event.time, event.parkingSpotId) }

    suspend fun handleVehicleParkedOnWrongSpot(event: VehicleParkedOnWrongEvent) =
        handle(event.parkingVehicle) { it.parkOnIncorrect(event.time, event.parkingSpotId) }

    suspend fun handleCustomerCreated(event: CustomerCreatedEvent) {
        createVehicleHistory(event.plateNumber, event.aggregateId)
    }

    suspend fun handleVehicleAdded(event: CustomerVehicleAddedEvent) {
        createVehicleHistory(event.plateNumber, event.aggregateId)
    }

    private suspend fun createVehicleHistory(
        plateNumber: PlateNumber,
        customerId: String,
    ) {
        VehicleHistoryReflection(
            plateNumber,
            customerId,
            listOf(HistoryItem(Instant.now(), HistoryType.CREATED)),
        ).also { repository.save(it) }
    }

    suspend fun handleVehicleRemoved(event: CustomerVehicleRemovedEvent) =
        handle(event.plateNumber) { it.removeVehicle() }

    suspend fun findByPlateNumber(plateNumber: PlateNumber) = loadVehicleHistory(plateNumber)

    suspend fun handleVehicleEntered(event: VehicleEnteredParkingLotEvent) =
        handle(event.plateNumber) { it.vehicleEntered(event.time, event.hasDisabilityCard) }

    suspend fun handleVehicleLeaved(event: VehicleLeavedParkingLotEvent) {
        val history = loadVehicleHistory(event.plateNumber)

        val invoice = invoiceService.invoice(history, event.time)
        history.vehicleLeaved(event.time)
            .chargeInvoice(invoice)
            .also { repository.save(it) }
    }

    private suspend inline fun handle(
        plateNumber: PlateNumber,
        crossinline block: (VehicleHistoryReflection) -> VehicleHistoryReflection,
    ) {
        mutex.withKeyLock(plateNumber) {
            loadVehicleHistory(plateNumber).also { customer ->
                block(customer).also { repository.save(it) }
            }
        }
    }

    private suspend fun loadVehicleHistory(plateNumber: PlateNumber): VehicleHistoryReflection =
        repository.findByPlateNumber(plateNumber)
            ?: throw IllegalArgumentException("History with id ${plateNumber.value} not found")
}

interface VehicleHistoryRepositoryPort {
    suspend fun save(vehicleHistory: VehicleHistoryReflection)

    suspend fun findByPlateNumber(plateNumber: PlateNumber): VehicleHistoryReflection?
}
