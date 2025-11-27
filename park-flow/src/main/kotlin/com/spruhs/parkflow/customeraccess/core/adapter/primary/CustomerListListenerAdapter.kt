package com.spruhs.parkflow.customeraccess.core.adapter.primary

import com.spruhs.parkflow.common.configs.EventExecutionStrategy
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerPaymentMethodChangedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleRemovedEvent
import com.spruhs.parkflow.customeraccess.core.application.CustomerListCommandPort
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CustomerListListenerAdapter(
    private val eventExecutionStrategy: EventExecutionStrategy,
    private val commandPort: CustomerListCommandPort,
) {
    @EventListener(
        CustomerCreatedEvent::class,
        CustomerPaymentMethodChangedEvent::class,
        CustomerVehicleAddedEvent::class,
        CustomerVehicleRemovedEvent::class,
        CustomerParkingSpotRentedEvent::class,
        CustomerParkingSpotCanceledEvent::class,
    )
    fun onEvent(event: BaseEvent) {
        eventExecutionStrategy.execute {
            commandPort.whenEvent(event)
        }
    }
}
