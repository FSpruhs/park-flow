package com.spruhs.parkflow.customeraccess.api

import com.spruhs.parkflow.common.es.AggregateRoot
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.Event
import com.spruhs.parkflow.common.es.EventSourcingUtils
import com.spruhs.parkflow.common.es.Serializer
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.customeraccess.core.domain.PaymentMethodId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import org.springframework.stereotype.Component
import java.time.LocalDate

data class CustomerCreatedEvent(
    override val aggregateId: String,
    val paymentMethodId: PaymentMethodId,
    val plateNumber: PlateNumber,
) : BaseEvent(aggregateId)

data class CustomerPaymentMethodChangedEvent(
    override val aggregateId: String,
    val paymentMethodId: PaymentMethodId,
) : BaseEvent(aggregateId)

data class CustomerVehicleAddedEvent(
    override val aggregateId: String,
    val plateNumber: PlateNumber,
) : BaseEvent(aggregateId)

data class CustomerVehicleRemovedEvent(
    override val aggregateId: String,
    val plateNumber: PlateNumber,
) : BaseEvent(aggregateId)

data class CustomerParkingSpotRentedEvent(
    override val aggregateId: String,
    val parkingSpotId: ParkingSpotId,
    val plateNumber: PlateNumber,
    val rentedAt: LocalDate,
) : BaseEvent(aggregateId)

data class CustomerParkingSpotCanceledEvent(
    override val aggregateId: String,
    val parkingSpotId: ParkingSpotId,
    val endOfRental: LocalDate,
) : BaseEvent(aggregateId)

enum class CustomerEvent {
    CUSTOMER_CREATED_V1,
    CUSTOMER_PAYMENT_METHOD_CHANGED_V1,
    CUSTOMER_VEHICLE_ADDED_V1,
    CUSTOMER_VEHICLE_REMOVED_V1,
    CUSTOMER_PARKING_SPOT_RENTED_V1,
    CUSTOMER_PARKING_SPOT_CANCELLED_V1,
}

@Component
class CustomerEventSerializer : Serializer {
    private val typeMapping: Map<Class<out BaseEvent>, CustomerEvent> =
        mapOf(
            CustomerCreatedEvent::class.java to CustomerEvent.CUSTOMER_CREATED_V1,
            CustomerPaymentMethodChangedEvent::class.java to CustomerEvent.CUSTOMER_PAYMENT_METHOD_CHANGED_V1,
            CustomerVehicleAddedEvent::class.java to CustomerEvent.CUSTOMER_VEHICLE_ADDED_V1,
            CustomerVehicleRemovedEvent::class.java to CustomerEvent.CUSTOMER_VEHICLE_REMOVED_V1,
            CustomerParkingSpotRentedEvent::class.java to CustomerEvent.CUSTOMER_PARKING_SPOT_RENTED_V1,
            CustomerParkingSpotCanceledEvent::class.java to CustomerEvent.CUSTOMER_PARKING_SPOT_CANCELLED_V1,
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

    override fun aggregateTypeName(): String = "CustomerAggregate"
}
