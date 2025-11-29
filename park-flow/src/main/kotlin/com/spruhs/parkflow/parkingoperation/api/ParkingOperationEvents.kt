package com.spruhs.parkflow.parkingoperation.api

import com.spruhs.parkflow.common.es.AggregateRoot
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.Event
import com.spruhs.parkflow.common.es.EventSourcingUtils
import com.spruhs.parkflow.common.es.Serializer
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.customeraccess.api.CustomerEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.GateActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateEvent
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.GateRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesAddedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesRemovedEvent
import com.spruhs.parkflow.parkingoperation.core.domain.Vehicle
import org.springframework.stereotype.Component
import java.time.Instant

data class VehicleArrivedEvent(override val aggregateId: String, val gateId: GateId, val vehicle: Vehicle) :
    BaseEvent(aggregateId)

data class VehicleEnteredParkingLotEvent(
    override val aggregateId: String,
    val gateId: GateId,
    val plateNumber: PlateNumber,
    val hasDisabilityCard: Boolean,
    val time: Instant = Instant.now(),
) : BaseEvent(aggregateId)

data class VehicleLeavedParkingLotEvent(
    override val aggregateId: String,
    val gateId: GateId,
    val plateNumber: PlateNumber,
    val time: Instant = Instant.now(),
) : BaseEvent(aggregateId)

data class VehicleParkedOnEvent(
    override val aggregateId: String,
    val parkingSpotId: ParkingSpotId,
    val plateNumber: PlateNumber,
    val time: Instant = Instant.now(),
) : BaseEvent(aggregateId)

data class VehicleParkedOffEvent(
    override val aggregateId: String,
    val parkingSpotId: ParkingSpotId,
    val plateNumber: PlateNumber,
    val time: Instant = Instant.now(),
) : BaseEvent(aggregateId)

data class ParkingSpotProvidedEvent(
    override val aggregateId: String,
    val plateNumber: PlateNumber,
    val parkingSpotId: ParkingSpotId,
) : BaseEvent(aggregateId)

data class ParkingSpotReprovidedEvent(
    override val aggregateId: String,
    val parkingSpotId: ParkingSpotId,
    val plateNumber: PlateNumber,
) : BaseEvent(aggregateId)

data class VehicleParkedOnWrongEvent(
    override val aggregateId: String,
    val parkingVehicle: PlateNumber,
    val reservedForVehicle: PlateNumber?,
    val parkingSpotId: ParkingSpotId,
    val time: Instant = Instant.now(),
) : BaseEvent(aggregateId)

enum class ParkingOperationEvent {
    VEHICLE_ARRIVED_V1,
    VEHICLE_ENTERED_PARKING_LOT_V1,
    VEHICLE_LEAVED_PARKING_LOT_V1,
    VEHICLE_PARKED_ON_V1,
    VEHICLE_PARKED_OFF_V1,
    PARKING_SPOT_PROVIDED_V1,
    VEHICLE_PARKED_ON_WRONG_V1,
    PARKING_SPOT_REPROVIDED_V1,
}

@Component
class ParkingOperationEventSerializer : Serializer {
    private val typeMapping: Map<Class<out BaseEvent>, String> =
        mapOf(
            // Parking operation events
            VehicleArrivedEvent::class.java to ParkingOperationEvent.VEHICLE_ARRIVED_V1.name,
            VehicleEnteredParkingLotEvent::class.java to ParkingOperationEvent.VEHICLE_ENTERED_PARKING_LOT_V1.name,
            VehicleLeavedParkingLotEvent::class.java to ParkingOperationEvent.VEHICLE_LEAVED_PARKING_LOT_V1.name,
            VehicleParkedOnEvent::class.java to ParkingOperationEvent.VEHICLE_PARKED_ON_V1.name,
            VehicleParkedOffEvent::class.java to ParkingOperationEvent.VEHICLE_PARKED_OFF_V1.name,
            ParkingSpotProvidedEvent::class.java to ParkingOperationEvent.PARKING_SPOT_PROVIDED_V1.name,
            VehicleParkedOnWrongEvent::class.java to
                ParkingOperationEvent.VEHICLE_PARKED_ON_WRONG_V1.name,
            ParkingSpotReprovidedEvent::class.java to ParkingOperationEvent.PARKING_SPOT_REPROVIDED_V1.name,
            // Gate events (imported)
            GateCreatedEvent::class.java to GateEvent.GATE_CREATED_V1.name,
            GateRemovedEvent::class.java to GateEvent.GATE_REMOVED_V1.name,
            GateActivatedEvent::class.java to GateEvent.GATE_ACTIVATED_V1.name,
            GateDeactivatedEvent::class.java to GateEvent.GATE_DEACTIVATED_V1.name,
            // Parking spot events (imported)
            ParkingSpotCreatedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_CREATED_V1.name,
            ParkingSpotRemovedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_REMOVED_V1.name,
            ParkingSpotActivatedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_ACTIVATED_V1.name,
            ParkingSpotDeactivatedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_DEACTIVATED_V1.name,
            ParkingSpotTypesAddedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_TYPED_ADDED_V1.name,
            ParkingSpotTypesRemovedEvent::class.java to ParkingSpotEvent.PARKING_SPOT_TYPES_REMOVED_V1.name,
            // Customer events (imported)
            CustomerParkingSpotRentedEvent::class.java to CustomerEvent.CUSTOMER_PARKING_SPOT_RENTED_V1.name,
            CustomerParkingSpotCanceledEvent::class.java to CustomerEvent.CUSTOMER_PARKING_SPOT_CANCELLED_V1.name,
        )

    private val classMapping: Map<String, Class<out BaseEvent>> =
        typeMapping.entries.associateBy(
            { it.value },
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
            eventType = type,
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

    override fun aggregateTypeName(): String = "ParkingOperatorAggregate"
}
