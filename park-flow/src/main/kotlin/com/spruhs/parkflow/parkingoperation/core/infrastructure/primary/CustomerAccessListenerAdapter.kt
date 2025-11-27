package com.spruhs.parkflow.parkingoperation.core.infrastructure.primary

import com.spruhs.parkflow.common.configs.AsyncEventExecutionStrategy
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.parkingoperation.core.application.ParkingOperationCommandPort
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class CustomerAccessListenerAdapter(
    private val commandPort: ParkingOperationCommandPort,
    private val eventExecutionStrategy: AsyncEventExecutionStrategy,
) {
    @EventListener(
        CustomerParkingSpotRentedEvent::class,
        CustomerParkingSpotCanceledEvent::class,
    )
    fun onEvent(event: BaseEvent) {
        eventExecutionStrategy.execute {
            commandPort.handleEvent(event)
        }
    }
}
