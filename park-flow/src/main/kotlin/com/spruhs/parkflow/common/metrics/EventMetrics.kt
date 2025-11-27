package com.spruhs.parkflow.common.metrics

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class EventMetrics(registry: MeterRegistry) {
    val rabbitReceived = registry.counter("events_rabbitmq_received_total")
    val springPublished = registry.counter("events_spring_published_total")
    val springConsumed = registry.counter("events_spring_consumed_total")
}
