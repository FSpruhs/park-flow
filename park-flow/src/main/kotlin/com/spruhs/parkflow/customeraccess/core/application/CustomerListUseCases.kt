package com.spruhs.parkflow.customeraccess.core.application

import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerPaymentMethodChangedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleRemovedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.customeraccess.core.domain.CustomerListProjection
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import org.springframework.stereotype.Component

@Component
class CustomerListCommandPort(private val service: CustomerListService) {
    suspend fun whenEvent(event: BaseEvent) {
        when (event) {
            is CustomerCreatedEvent -> service.handleCustomerCreated(event)
            is CustomerPaymentMethodChangedEvent -> service.handlePaymentMethodChanged(event)
            is CustomerVehicleAddedEvent -> service.handleVehicleAdded(event)
            is CustomerVehicleRemovedEvent -> service.handleVehicleRemoved(event)
            is CustomerParkingSpotRentedEvent -> service.handleParkingSpotRented(event)
            is CustomerParkingSpotCanceledEvent -> service.handleParkingSpotCanceled(event)

            else -> UnknownEventTypeException(event)
        }
    }
}

@Component
class CustomerListQueryPort(
    private val service: CustomerListService,
    private val plateNumberService: PlateNumberService,
) {
    suspend fun getCustomerList(): CustomerListProjection = service.getCustomerList()

    suspend fun isPlateNumberRegistered(plateNumber: PlateNumber) = plateNumberService.existsPlateNumber(plateNumber)

    suspend fun isParkingSpotRented(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) = service.isParkingSpotRented(parkingSpotId, plateNumber)
}
