package com.spruhs.parkflow.parkingoperation.core.application

import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.GateActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.GateRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesAddedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesRemovedEvent
import com.spruhs.parkflow.parkingoperation.api.ParkingSpotReprovidedEvent
import org.springframework.stereotype.Service

@Service
class ParkingOperationCommandPort(private val service: ParkingOperatorService) {
    suspend fun handleEvent(event: BaseEvent) {
        when (event) {
            is ParkingSpotCreatedEvent -> service.importEvent(event)
            is ParkingSpotRemovedEvent -> service.importEvent(event)
            is ParkingSpotTypesRemovedEvent -> service.importEvent(event)
            is ParkingSpotTypesAddedEvent -> service.importEvent(event)
            is ParkingSpotActivatedEvent -> service.importEvent(event)
            is ParkingSpotDeactivatedEvent -> service.importEvent(event)

            is GateCreatedEvent -> service.importEvent(event)
            is GateActivatedEvent -> service.importEvent(event)
            is GateDeactivatedEvent -> service.importEvent(event)
            is GateRemovedEvent -> service.importEvent(event)

            is CustomerParkingSpotRentedEvent -> service.importEvent(event)
            is CustomerParkingSpotCanceledEvent -> service.importEvent(event)

            is ParkingSpotReprovidedEvent -> service.handleParkingSpotReprovided(event)

            else -> throw UnknownEventTypeException(event)
        }
    }

    suspend fun vehicleArrived(
        gateId: GateId,
        plateNumber: PlateNumber,
        hasDisabilityCard: Boolean,
    ) = service.handleCarArrived(gateId, plateNumber, hasDisabilityCard)

    suspend fun carDroveThrough(
        gateId: GateId,
        plateNumber: PlateNumber,
    ) = service.handleCarDroveThrough(gateId, plateNumber)

    suspend fun vehicleParkedOff(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) = service.handleCarParkedOff(parkingSpotId, plateNumber)

    suspend fun carParkedOn(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) = service.handleCarParkedOn(parkingSpotId, plateNumber)

    fun clearCache() {
        service.clearCache()
    }
}
