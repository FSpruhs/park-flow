package com.spruhs.parksensormock.events

data class CarArrivedSensorEvent(val gateId: String, val plateNumber: String, val hasDisabilityCard: Boolean)

data class CarDroveThroughSensorEvent(val gateId: String, val plateNumber: String)

data class CarParkedOnSensorEvent(val parkingSpotId: String, val plateNumber: String)

data class CarParkedOffSensorEvent(val parkingSpotId: String, val plateNumber: String)

object RabbitMQConstants {
    const val EXCHANGE = "car.events"

    const val QUEUE_ARRIVED = "car.arrived"
    const val QUEUE_DROVE_THROUGH = "car.droveThrough"
    const val QUEUE_PARKED_ON = "car.parkedOn"
    const val QUEUE_PARKED_OFF = "car.parkedOff"

}
