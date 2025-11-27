package com.spruhs.parkflow.parkinginventory.api

import com.spruhs.parkflow.common.es.AggregateRoot
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.Event
import com.spruhs.parkflow.common.es.EventSourcingUtils
import com.spruhs.parkflow.common.es.Serializer
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.parkinginventory.core.domain.ActivationState
import com.spruhs.parkflow.parkinginventory.core.domain.GateName
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotName
import org.springframework.stereotype.Component

enum class ParkingSpotEvent {
    PARKING_SPOT_CREATED_V1,
    PARKING_SPOT_REMOVED_V1,
    PARKING_SPOT_TYPES_REMOVED_V1,
    PARKING_SPOT_TYPED_ADDED_V1,
    PARKING_SPOT_RENAMED_V1,
    PARKING_SPOT_ACTIVATED_V1,
    PARKING_SPOT_DEACTIVATED_V1,
}

data class ParkingSpotCreatedEvent(
    override val aggregateId: String,
    val parkingSpotName: ParkingSpotName,
    val spotTypes: Set<ParkingSpotType>,
    val spotState: ActivationState,
    val price: Price? = null,
) : BaseEvent(aggregateId)

data class ParkingSpotRemovedEvent(
    override val aggregateId: String,
    val name: ParkingSpotName,
) : BaseEvent(aggregateId)

data class ParkingSpotTypesRemovedEvent(
    override val aggregateId: String,
    val types: Set<ParkingSpotType>,
) : BaseEvent(aggregateId)

data class ParkingSpotTypesAddedEvent(
    override val aggregateId: String,
    val types: Set<ParkingSpotType>,
    val price: Price? = null,
) : BaseEvent(aggregateId)

data class ParkingSpotRenamedEvent(
    override val aggregateId: String,
    val newName: ParkingSpotName,
    val oldName: ParkingSpotName,
) : BaseEvent(aggregateId)

data class ParkingSpotActivatedEvent(override val aggregateId: String) : BaseEvent(aggregateId)

data class ParkingSpotDeactivatedEvent(override val aggregateId: String) : BaseEvent(aggregateId)

enum class GateEvent {
    GATE_CREATED_V1,
    GATE_ACTIVATED_V1,
    GATE_DEACTIVATED_V1,
    GATE_REMOVED_V1,
}

data class GateCreatedEvent(
    override val aggregateId: String,
    val gateType: GateType,
    val name: GateName,
) : BaseEvent(aggregateId)

data class GateActivatedEvent(override val aggregateId: String) : BaseEvent(aggregateId)

data class GateDeactivatedEvent(override val aggregateId: String) : BaseEvent(aggregateId)

data class GateRemovedEvent(override val aggregateId: String) : BaseEvent(aggregateId)

@Component
class GateEventSerializer : Serializer {
    private val typeMapping: Map<Class<out BaseEvent>, GateEvent> =
        mapOf(
            GateCreatedEvent::class.java to GateEvent.GATE_CREATED_V1,
            GateActivatedEvent::class.java to GateEvent.GATE_ACTIVATED_V1,
            GateDeactivatedEvent::class.java to GateEvent.GATE_DEACTIVATED_V1,
            GateRemovedEvent::class.java to GateEvent.GATE_REMOVED_V1,
        )

    private val classMapping: Map<String, Class<out BaseEvent>> =
        typeMapping.entries.associateBy(
            { it.value.name },
            { it.key },
        )

    override fun serialize(
        event: BaseEvent,
        aggregate: AggregateRoot,
    ): Event {
        val type =
            typeMapping[event::class.java]
                ?: throw UnknownEventTypeException(event)

        return Event(
            aggregate = aggregate,
            eventType = type.name,
            data = EventSourcingUtils.writeValueAsBytes(event),
            metadata = EventSourcingUtils.writeValueAsBytes(event.metadata),
        )
    }

    override fun deserialize(event: Event): BaseEvent {
        val clazz =
            classMapping[event.type]
                ?: throw UnknownEventTypeException("Unknown event type: ${event.type}")
        return EventSourcingUtils.readValue(event.data, clazz)
    }

    override fun aggregateTypeName(): String = "GateAggregate"
}

@Component
class ParkingSpotEventSerializer : Serializer {
    private val typeMapping: Map<Class<out BaseEvent>, ParkingSpotEvent> =
        mapOf(
            ParkingSpotCreatedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_CREATED_V1,
            ParkingSpotRemovedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_REMOVED_V1,
            ParkingSpotTypesRemovedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_TYPES_REMOVED_V1,
            ParkingSpotTypesAddedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_TYPED_ADDED_V1,
            ParkingSpotRenamedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_RENAMED_V1,
            ParkingSpotActivatedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_ACTIVATED_V1,
            ParkingSpotDeactivatedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_DEACTIVATED_V1,
        )

    private val classMapping: Map<String, Class<out BaseEvent>> =
        typeMapping.entries.associateBy(
            { it.value.name },
            { it.key },
        )

    override fun serialize(
        event: BaseEvent,
        aggregate: AggregateRoot,
    ): Event {
        val type =
            typeMapping[event::class.java]
                ?: throw UnknownEventTypeException(event)

        return Event(
            aggregate = aggregate,
            eventType = type.name,
            data = EventSourcingUtils.writeValueAsBytes(event),
            metadata = EventSourcingUtils.writeValueAsBytes(event.metadata),
        )
    }

    override fun deserialize(event: Event): BaseEvent {
        val clazz =
            classMapping[event.type]
                ?: throw UnknownEventTypeException("Unknown event type: ${event.type}")

        return EventSourcingUtils.readValue(event.data, clazz)
    }

    override fun aggregateTypeName(): String = "ParkingSpotAggregate"
}
