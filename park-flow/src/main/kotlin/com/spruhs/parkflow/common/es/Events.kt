package com.spruhs.parkflow.common.es

import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.common.metrics.EventMetrics
import com.spruhs.parkflow.common.metrics.ParkMetrics
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

fun interface EventPublisher {
    fun publish(events: List<BaseEvent>)
}

@Service
class EventPublisherImpl(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val eventMetrics: EventMetrics,
) : EventPublisher {
    private val log = getLogger(javaClass)

    override fun publish(events: List<BaseEvent>) {
        events.forEach {
            eventMetrics.springPublished.increment()

            log.info("Publish event: ${it.javaClass.simpleName}, aggregateId: ${it.aggregateId}")
            applicationEventPublisher.publishEvent(it)
        }
    }
}
