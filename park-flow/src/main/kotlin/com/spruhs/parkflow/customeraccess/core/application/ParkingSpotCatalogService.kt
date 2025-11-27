package com.spruhs.parkflow.customeraccess.core.application

import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.customeraccess.core.domain.ParkingSpotCatalogProjection
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesAddedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesRemovedEvent
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

@Service
class ParkingSpotCatalogService(private val repository: ParkingSpotCatalogRepositoryPort) {
    private val mutex = Mutex()
    private val reservedParkingSpots: MutableMap<ParkingSpotId, Instant> = mutableMapOf()

    @Scheduled(fixedRate = 60 * 1000)
    private fun cleanupExpiredReservations() {
        val now = Instant.now()
        reservedParkingSpots.entries.removeIf { (_, reservedAt) ->
            Duration.between(reservedAt, now).toMinutes() > 10
        }
    }

    suspend fun handleParkingSpotRented(event: CustomerParkingSpotRentedEvent) {
        handle { it.rentedParkingSpot(event.parkingSpotId.value) }
        reservedParkingSpots.remove(event.parkingSpotId)
    }

    suspend fun handleParkingSpotCreated(event: ParkingSpotCreatedEvent) {
        if (ParkingSpotType.Rentable in event.spotTypes) {
            handle { it.addedParkingSpot(event.aggregateId, event.price, event.spotTypes) }
        }
    }

    suspend fun handleParkingSpotTypeAdded(event: ParkingSpotTypesAddedEvent) =
        handle {
            if (ParkingSpotType.Rentable in event.types) {
                it.addedParkingSpot(event.aggregateId, event.price, event.types)
            }
            if (ParkingSpotType.Electric in event.types) {
                it.addElectrical(event.aggregateId)
            }
        }

    suspend fun handleParkingSpotRemoved(event: ParkingSpotRemovedEvent) =
        handle { it.removedParkingSpot(event.aggregateId) }

    suspend fun handleParkingSpotDeactivated(event: ParkingSpotDeactivatedEvent) =
        handle { it.deactivatedParkingSpot(event.aggregateId) }

    suspend fun handleParkingSpotActivated(event: ParkingSpotActivatedEvent) =
        handle { it.activatedParkingSpot(event.aggregateId) }

    suspend fun handleParkingSpotCancelled(event: CustomerParkingSpotCanceledEvent) =
        handle { it.cancelledParkingSpot(event.parkingSpotId.value, event.endOfRental) }

    suspend fun handleParkingSpotTypeRemoved(event: ParkingSpotTypesRemovedEvent) =
        handle {
            if (ParkingSpotType.Rentable in event.types) {
                it.removedParkingSpot(event.aggregateId)
            }
            if (ParkingSpotType.Electric in event.types) {
                it.removedElectrical(event.aggregateId)
            }
        }

    private suspend inline fun handle(block: (ParkingSpotCatalogProjection) -> Unit) {
        mutex.withLock {
            loadCatalog().also {
                block(it)
                repository.save(it)
            }
        }
    }

    private suspend fun loadCatalog(): ParkingSpotCatalogProjection = repository.getCatalog()

    suspend fun reserve(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) {
        validateReserveParkingSpot(parkingSpotId, plateNumber)

        reservedParkingSpots[parkingSpotId] = Instant.now()
    }

    private suspend fun validateReserveParkingSpot(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) {
        require(parkingSpotId !in reservedParkingSpots) { "Parking spot already reserved" }
        val parkingSpotToReserve =
            loadCatalog().parkingSpotCatalogItems
                .find { it.parkingSpotId == parkingSpotId.value }

        requireNotNull(parkingSpotToReserve) { "Parking spot not not found" }
        require(parkingSpotToReserve.isActive) { "Parking spot not active" }
        requireNotNull(parkingSpotToReserve.availableFrom) { "Parking spot already rented" }
        require(parkingSpotToReserve.availableFrom.minusDays(1).isBefore(LocalDate.now())) {
            "Parking spot rented till"
        }
        if (parkingSpotToReserve.isElectrical) {
            require(plateNumber.isElectrical()) { "Vehicle must be electric" }
        }
    }

    suspend fun isElectrical(parkingSpotId: ParkingSpotId) =
        loadCatalog().parkingSpotCatalogItems
            .find { it.parkingSpotId == parkingSpotId.value }
            ?.isElectrical
            ?: false
}

interface ParkingSpotCatalogRepositoryPort {
    suspend fun getCatalog(): ParkingSpotCatalogProjection

    suspend fun save(catalog: ParkingSpotCatalogProjection)
}
