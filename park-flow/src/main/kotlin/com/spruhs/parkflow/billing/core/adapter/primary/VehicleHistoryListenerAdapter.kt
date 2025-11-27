package com.spruhs.parkflow.billing.core.adapter.primary

import com.spruhs.parkflow.billing.core.application.VehicleHistoryCommandPort
import com.spruhs.parkflow.common.configs.AsyncEventExecutionStrategy
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleRemovedEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleEnteredParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleLeavedParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOffEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnWrongParkingSpotEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class VehicleHistoryListenerAdapter(
    private val commandPort: VehicleHistoryCommandPort,
    private val eventExecutionStrategy: AsyncEventExecutionStrategy,
) {
    @EventListener(
        VehicleEnteredParkingLotEvent::class,
        VehicleLeavedParkingLotEvent::class,
        VehicleParkedOnEvent::class,
        VehicleParkedOffEvent::class,
        VehicleParkedOnWrongParkingSpotEvent::class,
        CustomerCreatedEvent::class,
        CustomerVehicleAddedEvent::class,
        CustomerVehicleRemovedEvent::class,
    )
    fun onEvent(event: BaseEvent) {
        eventExecutionStrategy.execute {
            commandPort.handleEvent(event)
        }
    }
}
