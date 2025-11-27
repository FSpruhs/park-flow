package com.spruhs.parkflow.customeraccess.core.application

import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import org.springframework.stereotype.Service

@Service
class PlateNumberCommandPort(private val service: PlateNumberService) {
    suspend fun handleEvent(event: BaseEvent) {
        when (event) {
            is CustomerVehicleAddedEvent -> service.handlePlateNumberAdded(event.plateNumber)
            is CustomerCreatedEvent -> service.handlePlateNumberAdded(event.plateNumber)

            else -> UnknownEventTypeException(event)
        }
    }
}
