package com.spruhs.parkflow.customeraccess.core.adapter.primary

import com.spruhs.parkflow.customeraccess.api.CustomerApi
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.customeraccess.core.application.CustomerListQueryPort
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import org.springframework.stereotype.Component

@Component
class CustomerApiAdapter(private val queryPort: CustomerListQueryPort) : CustomerApi {
    override suspend fun isPlateNumberRegistered(plateNumber: PlateNumber) =
        queryPort.isPlateNumberRegistered(plateNumber)

    override suspend fun isParkingSpotRented(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) = queryPort.isParkingSpotRented(parkingSpotId, plateNumber)
}
