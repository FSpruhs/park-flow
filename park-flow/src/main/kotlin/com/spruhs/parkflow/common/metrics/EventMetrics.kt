package com.spruhs.parkflow.common.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class EventMetrics(registry: MeterRegistry) {
    val rabbitReceived: Counter = registry.counter("events_rabbitmq_received_total")
    val springPublished: Counter = registry.counter("events_spring_published_total")
    val springConsumed: Counter = registry.counter("events_spring_consumed_total")
}
