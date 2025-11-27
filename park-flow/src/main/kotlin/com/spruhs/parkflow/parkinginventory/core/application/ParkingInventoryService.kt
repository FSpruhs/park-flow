package com.spruhs.parkflow.parkinginventory.core.application

import com.spruhs.parkflow.parkinginventory.api.GateActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRenamedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesAddedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesRemovedEvent
import com.spruhs.parkflow.parkinginventory.core.domain.GateName
import com.spruhs.parkflow.parkinginventory.core.domain.GateProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingInventoryProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotName
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotProjection
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import kotlin.collections.component1
import kotlin.collections.component2

@Service
class ParkingInventoryService(private val repository: ParkingInventoryRepositoryPort) {
    private val mutex = Mutex()
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
        require(!loadInventory().existsGateName(name)) { "Gate name already exists" }

        reservedGateNames[name] = Instant.now()
    }

    suspend fun reserveParkingSpotName(name: ParkingSpotName) {
        require(name !in reservedParkingSpotNames.keys) { "Parking spot name already reserved" }
        require(!loadInventory().existsParkingSpotName(name)) { "Parking spot name already exists" }

        reservedParkingSpotNames[name] = Instant.now()
    }

    private fun isReservationTimeOver(
        reservedAt: Instant,
        now: Instant,
    ) = Duration.between(reservedAt, now).toMinutes() > RESERVATION_TIME_IN_MINUTES

    suspend fun getInventory() = loadInventory()

    suspend fun handleParkingSpotCreatedEvent(event: ParkingSpotCreatedEvent) {
        handle { it.addParkingSpot(event.toProjection()) }

        reservedParkingSpotNames.remove(event.parkingSpotName)
    }

    suspend fun handleParkingSpotRenamedEvent(event: ParkingSpotRenamedEvent) {
        handle { it.renameParkingSpot(event.aggregateId, event.newName) }

        reservedParkingSpotNames.remove(event.newName)
    }

    suspend fun handleGateCreatedEvent(event: GateCreatedEvent) {
        handle { it.addGate(event.toProjection()) }

        reservedGateNames.remove(event.name)
    }

    suspend fun handleParkingSpotRemoved(event: ParkingSpotRemovedEvent) =
        handle { it.removeParkingSpot(event.aggregateId) }

    suspend fun handleGateActivated(event: GateActivatedEvent) = handle { it.activateGate(event.aggregateId) }

    suspend fun handleGateDeactivated(event: GateDeactivatedEvent) = handle { it.deactivateGate(event.aggregateId) }

    suspend fun handleGateRemoved(event: GateRemovedEvent) = handle { it.removeGate(event.aggregateId) }

    suspend fun handleParkingSpotTypesAdded(event: ParkingSpotTypesAddedEvent) =
        handle { it.addParkingSpotType(event.aggregateId, event.types, event.price?.value.toString()) }

    suspend fun handleParkingSpotTypesRemoved(event: ParkingSpotTypesRemovedEvent) =
        handle { it.removeParkingSpotType(event.aggregateId, event.types) }

    suspend fun handleParkingSpotActivated(event: ParkingSpotActivatedEvent) =
        handle { it.activateParkingSpot(event.aggregateId) }

    suspend fun handleParkingSpotDeactivated(event: ParkingSpotDeactivatedEvent) =
        handle { it.deactivateParkingSpot(event.aggregateId) }

    private suspend inline fun handle(block: (ParkingInventoryProjection) -> Unit) {
        mutex.withLock {
            loadInventory().also {
                block(it)
                repository.save(it)
            }
        }
    }

    private suspend fun loadInventory(): ParkingInventoryProjection = repository.getInventory()

    companion object {
        private const val RESERVATION_TIME_IN_MINUTES = 5
    }
}

interface ParkingInventoryRepositoryPort {
    suspend fun getInventory(): ParkingInventoryProjection

    suspend fun save(inventoryProjection: ParkingInventoryProjection)
}

private fun ParkingSpotCreatedEvent.toProjection() =
    ParkingSpotProjection(
        parkingSpotId = aggregateId,
        name = parkingSpotName.value,
        types = spotTypes.toList(),
        price = price?.value?.toString(),
    )

private fun GateCreatedEvent.toProjection() =
    GateProjection(
        gateId = aggregateId,
        name = this.name.value,
        type = this.gateType,
    )
