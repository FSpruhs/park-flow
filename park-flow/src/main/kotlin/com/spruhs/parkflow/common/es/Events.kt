package com.spruhs.parkflow.common.es

import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.common.metrics.EventMetrics
import com.spruhs.parkflow.common.metrics.ParkMetrics
import com.spruhs.parkflow.parkingoperation.api.VehicleEnteredParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleLeavedParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnWrongEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

fun interface EventPublisher {
    fun publish(events: List<BaseEvent>)
}

@Service
class EventPublisherImpl(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val eventMetrics: EventMetrics,
    private val parkMetrics: ParkMetrics
) : EventPublisher {
    private val log = getLogger(javaClass)

    override fun publish(events: List<BaseEvent>) {
        events.forEach {
            eventMetrics.springPublished.increment()

            when (it) {
                is VehicleEnteredParkingLotEvent -> parkMetrics.carEntered()
                is VehicleLeavedParkingLotEvent -> parkMetrics.carLeft()
                is VehicleParkedOnEvent -> parkMetrics.correctParkedVehicles.increment()
                is VehicleParkedOnWrongEvent -> parkMetrics.wrongParkedVehicles.increment()
            }

            log.info("Publish event: ${it.javaClass.simpleName}, aggregateId: ${it.aggregateId}")
            applicationEventPublisher.publishEvent(it)
        }
    }
}
