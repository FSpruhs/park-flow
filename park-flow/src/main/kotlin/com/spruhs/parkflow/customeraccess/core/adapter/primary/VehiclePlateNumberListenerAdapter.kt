package com.spruhs.parkflow.customeraccess.core.adapter.primary

import com.spruhs.parkflow.common.configs.EventExecutionStrategy
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import com.spruhs.parkflow.customeraccess.core.application.PlateNumberCommandPort
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class VehiclePlateNumberListenerAdapter(
    private val eventExecutionStrategy: EventExecutionStrategy,
    private val commandPort: PlateNumberCommandPort,
) {
    @EventListener(
        CustomerVehicleAddedEvent::class,
        CustomerCreatedEvent::class,
    )
    fun onEvent(event: BaseEvent) {
        eventExecutionStrategy.execute {
            commandPort.handleEvent(event)
        }
    }
}
