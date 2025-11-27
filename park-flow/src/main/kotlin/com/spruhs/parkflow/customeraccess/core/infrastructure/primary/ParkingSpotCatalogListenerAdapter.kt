package com.spruhs.parkflow.customeraccess.core.infrastructure.primary

import com.spruhs.parkflow.common.configs.EventExecutionStrategy
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.core.application.ParkingSpotCatalogCommandPort
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesAddedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesRemovedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ParkingSpotCatalogListenerAdapter(
    private val eventExecutionStrategy: EventExecutionStrategy,
    private val commandPort: ParkingSpotCatalogCommandPort,
) {
    @EventListener(
        CustomerParkingSpotRentedEvent::class,
        CustomerParkingSpotCanceledEvent::class,
        ParkingSpotCreatedEvent::class,
        ParkingSpotRemovedEvent::class,
        ParkingSpotTypesRemovedEvent::class,
        ParkingSpotTypesAddedEvent::class,
        ParkingSpotActivatedEvent::class,
        ParkingSpotDeactivatedEvent::class,
    )
    fun onEvent(event: BaseEvent) {
        eventExecutionStrategy.execute {
            commandPort.handleEvent(event)
        }
    }
}
