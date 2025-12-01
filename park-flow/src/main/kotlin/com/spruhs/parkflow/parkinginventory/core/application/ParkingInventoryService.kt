package com.spruhs.parkflow.parkinginventory.core.application

import com.spruhs.parkflow.common.helper.KeyedMutex
import com.spruhs.parkflow.parkinginventory.api.GateActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.GateRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRenamedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesAddedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesRemovedEvent
import com.spruhs.parkflow.parkinginventory.core.domain.ActivationState
import com.spruhs.parkflow.parkinginventory.core.domain.GateName
import com.spruhs.parkflow.parkinginventory.core.domain.GateNotFoundException
import com.spruhs.parkflow.parkinginventory.core.domain.GateProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingInventoryProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotName
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotNotFoundException
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotProjection
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import kotlin.collections.component1
import kotlin.collections.component2

@Service
class ParkingInventoryService(private val repository: ParkingInventoryRepositoryPort) {
    private val mutex = KeyedMutex<String>()
    private val reservedGateNames: MutableMap<GateName, Instant> = mutableMapOf()
    private val reservedParkingSpotNames: MutableMap<ParkingSpotName, Instant> = mutableMapOf()

    @Scheduled(fixedRate = 60 * 1000)
    private fun cleanupExpiredReservations() {
        val now = Instant.now()
        reservedGateNames.entries.removeIf { (_, reservedAt) -> isReservationTimeOver(reservedAt, now) }

        reservedParkingSpotNames.entries.removeIf { (_, reservedAt) -> isReservationTimeOver(reservedAt, now) }
    }

    suspend fun reserveGateName(name: GateName) {
        require(name !in reservedGateNames.keys) { "Gate name already exists" }
        require(!repository.existsGateName(name)) { "Gate name already exists" }

        reservedGateNames[name] = Instant.now()
    }

    suspend fun reserveParkingSpotName(name: ParkingSpotName) {
        require(name !in reservedParkingSpotNames.keys) { "Parking spot name already reserved" }
        require(!repository.existsParkingSpotName(name)) { "Parking spot name already exists" }

        reservedParkingSpotNames[name] = Instant.now()
    }

    private fun isReservationTimeOver(
        reservedAt: Instant,
        now: Instant,
    ) = Duration.between(reservedAt, now).toMinutes() > RESERVATION_TIME_IN_MINUTES

    suspend fun getInventory() = loadInventory()

    suspend fun handleParkingSpotCreatedEvent(event: ParkingSpotCreatedEvent) {
        repository.save(event.toProjection())

        reservedParkingSpotNames.remove(event.parkingSpotName)
    }

    suspend fun handleParkingSpotRenamedEvent(event: ParkingSpotRenamedEvent) =
        handleParkingSpot(event.aggregateId) { it.copy(name = event.newName.value) }

    suspend fun handleGateCreatedEvent(event: GateCreatedEvent) {
        repository.save(event.toProjection())

        reservedGateNames.remove(event.name)
    }

    suspend fun handleParkingSpotRemoved(event: ParkingSpotRemovedEvent) =
        repository.removeParkingSpot(event.aggregateId)

    suspend fun handleGateActivated(event: GateActivatedEvent) =
        handleGate(event.aggregateId) { it.copy(state = ActivationState.ACTIVE) }

    suspend fun handleGateDeactivated(event: GateDeactivatedEvent) =
        handleGate(event.aggregateId) { it.copy(state = ActivationState.INACTIVE) }

    suspend fun handleGateRemoved(event: GateRemovedEvent) = repository.removeGate(event.aggregateId)

    suspend fun handleParkingSpotTypesAdded(event: ParkingSpotTypesAddedEvent) =
        handleParkingSpot(event.aggregateId) { spot ->
            val newTypes = spot.types + event.types.map { it.toValue() }
            val newPrice = if (ParkingSpotType.Rentable in event.types) event.price?.value.toString() else spot.price
            spot.copy(types = newTypes, price = newPrice)
        }

    suspend fun handleParkingSpotTypesRemoved(event: ParkingSpotTypesRemovedEvent) =
        handleParkingSpot(event.aggregateId) { spot ->
            val newTypes = spot.types - event.types.map { it.toValue() }.toSet()
            val newPrice = if (ParkingSpotType.Rentable in event.types) null else spot.price
            spot.copy(types = newTypes, price = newPrice)
        }

    suspend fun handleParkingSpotActivated(event: ParkingSpotActivatedEvent) =
        handleParkingSpot(event.aggregateId) { it.copy(state = ActivationState.ACTIVE) }

    suspend fun handleParkingSpotDeactivated(event: ParkingSpotDeactivatedEvent) =
        handleParkingSpot(event.aggregateId) { it.copy(state = ActivationState.INACTIVE) }

    private suspend inline fun handleGate(
        gateId: String,
        crossinline block: (GateProjection) -> GateProjection,
    ) {
        mutex.withKeyLock(gateId) {
            loadGate(gateId).also { gate ->
                block(gate).also { repository.save(it) }
            }
        }
    }

    private suspend inline fun handleParkingSpot(
        parkingSpotId: String,
        crossinline block: (ParkingSpotProjection) -> ParkingSpotProjection,
    ) {
        mutex.withKeyLock(parkingSpotId) {
            loadParkingSpot(parkingSpotId).also { spot ->
                block(spot).also { repository.save(it) }
            }
        }
    }

    private suspend fun loadGate(id: String): GateProjection =
        repository.getGate(id) ?: throw GateNotFoundException(GateId(id))

    private suspend fun loadParkingSpot(id: String): ParkingSpotProjection =
        repository.getParkingSpot(id) ?: throw ParkingSpotNotFoundException(ParkingSpotId(id))

    private suspend fun loadInventory(): ParkingInventoryProjection = repository.getInventory()

    companion object {
        private const val RESERVATION_TIME_IN_MINUTES = 5
    }
}

interface ParkingInventoryRepositoryPort {
    suspend fun getInventory(): ParkingInventoryProjection

    suspend fun getGate(gateId: String): GateProjection?

    suspend fun getParkingSpot(parkingSpotId: String): ParkingSpotProjection?

    suspend fun save(gateProjection: GateProjection)

    suspend fun save(parkingSpotProjection: ParkingSpotProjection)

    suspend fun existsGateName(name: GateName): Boolean

    suspend fun existsParkingSpotName(name: ParkingSpotName): Boolean

    suspend fun removeParkingSpot(parkingSpotId: String)

    suspend fun removeGate(gateId: String)
}

private fun ParkingSpotCreatedEvent.toProjection() =
    ParkingSpotProjection(
        parkingSpotId = aggregateId,
        name = parkingSpotName.value,
        types = spotTypes.map { it.toValue() },
        price = price?.value?.toString(),
    )

private fun GateCreatedEvent.toProjection() =
    GateProjection(
        gateId = aggregateId,
        name = this.name.value,
        type = this.gateType,
    )
