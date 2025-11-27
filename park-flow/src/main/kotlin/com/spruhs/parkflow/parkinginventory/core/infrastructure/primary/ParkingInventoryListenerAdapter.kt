package com.spruhs.parkflow.parkinginventory.core.infrastructure.primary

import com.spruhs.parkflow.common.configs.EventExecutionStrategy
import com.spruhs.parkflow.common.es.BaseEvent
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
import com.spruhs.parkflow.parkinginventory.core.application.ParkingInventoryCommandPort
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component("parkingInventoryInventoryListenerAdapter")
class ParkingInventoryListenerAdapter(
    private val eventExecutionStrategy: EventExecutionStrategy,
    private val commandPort: ParkingInventoryCommandPort,
) {
    @EventListener(
        ParkingSpotCreatedEvent::class,
        ParkingSpotRemovedEvent::class,
        ParkingSpotTypesRemovedEvent::class,
        ParkingSpotTypesAddedEvent::class,
        ParkingSpotRenamedEvent::class,
        ParkingSpotActivatedEvent::class,
        ParkingSpotDeactivatedEvent::class,
        GateCreatedEvent::class,
        GateActivatedEvent::class,
        GateDeactivatedEvent::class,
        GateRemovedEvent::class,
    )
    fun onEvent(event: BaseEvent) {
        eventExecutionStrategy.execute {
            commandPort.handleEvent(event)
        }
    }
}
