package com.spruhs.parkflowsimulator.publisher

import com.spruhs.parksensormock.events.CarArrivedSensorEvent
import com.spruhs.parksensormock.events.CarDroveThroughSensorEvent
import com.spruhs.parksensormock.events.CarParkedOffSensorEvent
import com.spruhs.parksensormock.events.CarParkedOnSensorEvent
import com.spruhs.parksensormock.events.RabbitMQConstants
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class VehicleEventPublisher(private val rabbitTemplate: RabbitTemplate) {


    suspend fun sendArrived(event: CarArrivedSensorEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE, RabbitMQConstants.QUEUE_ARRIVED, event)
    }

    suspend fun sendDroveThrough(event: CarDroveThroughSensorEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE, RabbitMQConstants.QUEUE_DROVE_THROUGH, event)
    }

    suspend fun sendParkedOn(event: CarParkedOnSensorEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE, RabbitMQConstants.QUEUE_PARKED_ON, event)
    }

    suspend fun sendParkedOff(event: CarParkedOffSensorEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE, RabbitMQConstants.QUEUE_PARKED_OFF, event)
    }
}
