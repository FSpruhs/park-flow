package com.spruhs.parkflow.common.metrics

import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class ParkMetrics(registry: MeterRegistry) {
    private val vehiclesInParkingLot = AtomicInteger(0)
    val correctParkedVehicles = registry.counter("vehicles_correct_parked")
    val wrongParkedVehicles = registry.counter("vehicles_wrong_parked")

    val enteredParkingSpot: Gauge =
        Gauge.builder("vehicles_in_parking_lot") {
            vehiclesInParkingLot.get().toDouble()
        }.register(registry)

    fun carEntered() {
        vehiclesInParkingLot.incrementAndGet()
    }

    fun carLeft() {
        vehiclesInParkingLot.decrementAndGet()
    }
}
