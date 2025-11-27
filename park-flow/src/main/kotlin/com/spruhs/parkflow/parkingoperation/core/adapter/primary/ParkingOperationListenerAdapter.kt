package com.spruhs.parkflow.parkingoperation.core.adapter.primary

import com.spruhs.parkflow.common.configs.EventExecutionStrategy
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.metrics.ParkMetrics
import com.spruhs.parkflow.parkingoperation.api.ParkingSpotReprovidedEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleEnteredParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleLeavedParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnWrongEvent
import com.spruhs.parkflow.parkingoperation.core.application.ParkingOperationCommandPort
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ParkingOperationListenerAdapter(
    private val eventExecutionStrategy: EventExecutionStrategy,
    private val parkMetrics: ParkMetrics,
    private val commandPort: ParkingOperationCommandPort,
) {
    @EventListener(ParkingSpotReprovidedEvent::class)
    fun onEvent(event: ParkingSpotReprovidedEvent) {
        eventExecutionStrategy.execute {
            commandPort.handleEvent(event)
        }
    }

    @EventListener
    fun onEvent(event: BaseEvent) {
        eventExecutionStrategy.execute {
            when (event) {
                is VehicleEnteredParkingLotEvent -> parkMetrics.carEntered()
                is VehicleLeavedParkingLotEvent -> parkMetrics.carLeft()
                is VehicleParkedOnEvent -> parkMetrics.correctParkedVehicles.increment()
                is VehicleParkedOnWrongEvent -> parkMetrics.wrongParkedVehicles.increment()
            }
        }
    }
}
