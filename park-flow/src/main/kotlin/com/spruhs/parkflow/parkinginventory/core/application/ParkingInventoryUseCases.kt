package com.spruhs.parkflow.parkinginventory.core.application

import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.parkinginventory.api.GateActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateDeactivatedEvent
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
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingInventoryProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotProjection
import org.springframework.stereotype.Component

@Component
class ParkingInventoryQueryPort(private val service: ParkingInventoryService) {
    suspend fun getInventory(): ParkingInventoryProjection = service.getInventory()

    suspend fun getParkingSpotTypes(parkingSpotId: ParkingSpotId) =
        getInventory().parkingSpots.find(parkingSpotId)
            ?.types
            ?.map { ParkingSpotType.fromString(it) }
            ?: emptyList()

    private fun List<ParkingSpotProjection>.find(parkingSpotId: ParkingSpotId) =
        firstOrNull { it.parkingSpotId == parkingSpotId.value }
}

@Component
class ParkingInventoryCommandPort(private val service: ParkingInventoryService) {
    suspend fun handleEvent(event: BaseEvent) {
        when (event) {
            is ParkingSpotCreatedEvent -> service.handleParkingSpotCreatedEvent(event)
            is ParkingSpotRemovedEvent -> service.handleParkingSpotRemoved(event)
            is ParkingSpotTypesRemovedEvent -> service.handleParkingSpotTypesRemoved(event)
            is ParkingSpotTypesAddedEvent -> service.handleParkingSpotTypesAdded(event)
            is ParkingSpotRenamedEvent -> service.handleParkingSpotRenamedEvent(event)
            is ParkingSpotActivatedEvent -> service.handleParkingSpotActivated(event)
            is ParkingSpotDeactivatedEvent -> service.handleParkingSpotDeactivated(event)

            is GateCreatedEvent -> service.handleGateCreatedEvent(event)
            is GateActivatedEvent -> service.handleGateActivated(event)
            is GateDeactivatedEvent -> service.handleGateDeactivated(event)
            is GateRemovedEvent -> service.handleGateRemoved(event)

            else -> throw UnknownEventTypeException(event)
        }
    }
}
