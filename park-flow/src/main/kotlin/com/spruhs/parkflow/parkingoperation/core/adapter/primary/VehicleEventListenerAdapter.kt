package com.spruhs.parkflow.parkingoperation.core.adapter.primary

import com.spruhs.parkflow.common.configs.EventExecutionStrategy
import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.common.metrics.EventMetrics
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkingoperation.core.application.ParkingOperationCommandPort
import com.spruhs.parksensormock.events.CarArrivedSensorEvent
import com.spruhs.parksensormock.events.CarDroveThroughSensorEvent
import com.spruhs.parksensormock.events.CarParkedOffSensorEvent
import com.spruhs.parksensormock.events.CarParkedOnSensorEvent
import com.spruhs.parksensormock.events.RabbitMQConstants.QUEUE_ARRIVED
import com.spruhs.parksensormock.events.RabbitMQConstants.QUEUE_DROVE_THROUGH
import com.spruhs.parksensormock.events.RabbitMQConstants.QUEUE_PARKED_OFF
import com.spruhs.parksensormock.events.RabbitMQConstants.QUEUE_PARKED_ON
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class VehicleEventListenerAdapter(
    private val commandPort: ParkingOperationCommandPort,
    private val eventExecutionStrategy: EventExecutionStrategy,
    private val metrics: EventMetrics,
) {
    private val log = getLogger(javaClass)

    @RabbitListener(queues = [QUEUE_ARRIVED])
    fun handleVehicleArrived(event: CarArrivedSensorEvent) {
        log.info("VehicleArrivedEvent received: $event")
        metrics.rabbitReceived.increment()
        eventExecutionStrategy.execute {
            commandPort.vehicleArrived(GateId(event.gateId), PlateNumber(event.plateNumber), event.hasDisabilityCard)
        }
    }

    @RabbitListener(queues = [QUEUE_DROVE_THROUGH])
    fun handleVehicleDroveThrough(event: CarDroveThroughSensorEvent) {
        log.info("VehicleDroveThroughEvent received: $event")
        metrics.rabbitReceived.increment()
        eventExecutionStrategy.execute {
            commandPort.carDroveThrough(GateId(event.gateId), PlateNumber(event.plateNumber))
        }
    }

    @RabbitListener(queues = [QUEUE_PARKED_ON])
    fun handleVehicleParkedOn(event: CarParkedOnSensorEvent) {
        log.info("VehicleParkedOnEvent received: $event")
        metrics.rabbitReceived.increment()
        eventExecutionStrategy.execute {
            commandPort.carParkedOn(ParkingSpotId(event.parkingSpotId), PlateNumber(event.plateNumber))
        }
    }

    @RabbitListener(queues = [QUEUE_PARKED_OFF])
    fun handleVehicleParkedOff(event: CarParkedOffSensorEvent) {
        log.info("VehicleParkedOffEvent received: $event")
        metrics.rabbitReceived.increment()
        eventExecutionStrategy.execute {
            commandPort.vehicleParkedOff(ParkingSpotId(event.parkingSpotId), PlateNumber(event.plateNumber))
        }
    }
}
