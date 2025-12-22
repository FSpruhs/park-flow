package com.spruhs.parkflow.customeraccess.core.application

import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.core.domain.ParkingSpotCatalogProjection
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesAddedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesRemovedEvent
import org.springframework.stereotype.Component

@Component
class ParkingSpotCatalogQueryPort(private val repository: ParkingSpotCatalogRepositoryPort) {
    suspend fun getCatalog(): ParkingSpotCatalogProjection {
        return repository.getCatalog()
    }
}

@Component
class ParkingSpotCatalogCommandPort(private val service: ParkingSpotCatalogService) {
    suspend fun handleEvent(event: BaseEvent) {
        when (event) {
            is CustomerParkingSpotRentedEvent -> service.handleParkingSpotRented(event)
            is CustomerParkingSpotCanceledEvent -> service.handleParkingSpotCancelled(event)
            is ParkingSpotCreatedEvent -> service.handleParkingSpotCreated(event)
            is ParkingSpotRemovedEvent -> service.handleParkingSpotRemoved(event)
            is ParkingSpotTypesRemovedEvent -> service.handleParkingSpotTypeRemoved(event)
            is ParkingSpotTypesAddedEvent -> service.handleParkingSpotTypeAdded(event)
            is ParkingSpotActivatedEvent -> service.handleParkingSpotActivated(event)
            is ParkingSpotDeactivatedEvent -> service.handleParkingSpotDeactivated(event)

            else -> throw UnknownEventTypeException(event)
        }
    }
}
