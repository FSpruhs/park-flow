package com.spruhs.parkflow.billing.core.application

import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleRemovedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkingoperation.api.VehicleEnteredParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleLeavedParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOffEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnWrongEvent
import org.springframework.stereotype.Component

@Component
class VehicleHistoryCommandPort(private val service: VehicleHistoryService) {
    suspend fun handleEvent(event: BaseEvent) {
        when (event) {
            is VehicleLeavedParkingLotEvent -> service.handleVehicleLeaved(event)
            is VehicleEnteredParkingLotEvent -> service.handleVehicleEntered(event)
            is VehicleParkedOnEvent -> service.handleCarParkedOn(event)
            is VehicleParkedOffEvent -> service.handleCarParkedOff(event)
            is VehicleParkedOnWrongEvent -> service.handleVehicleParkedOnWrongSpot(event)
            is CustomerCreatedEvent -> service.handleCustomerCreated(event)
            is CustomerVehicleAddedEvent -> service.handleVehicleAdded(event)
            is CustomerVehicleRemovedEvent -> service.handleVehicleRemoved(event)

            else -> UnknownEventTypeException(event)
        }
    }
}

@Component
class VehicleHistoryQueryPort(private val service: VehicleHistoryService) {
    suspend fun findByPlateNumber(plateNumber: PlateNumber) = service.findByPlateNumber(plateNumber)
}
