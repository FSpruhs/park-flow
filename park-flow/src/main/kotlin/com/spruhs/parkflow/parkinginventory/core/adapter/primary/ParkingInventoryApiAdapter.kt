package com.spruhs.parkflow.parkinginventory.core.adapter.primary

import com.spruhs.parkflow.parkinginventory.api.ParkingInventoryApi
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.core.application.ParkingInventoryQueryPort
import org.springframework.stereotype.Component

@Component
class ParkingInventoryApiAdapter(
    private val queryPort: ParkingInventoryQueryPort,
) : ParkingInventoryApi {
    override suspend fun getParkingSpotTypes(parkingSpotId: ParkingSpotId) =
        queryPort.getParkingSpotTypes(parkingSpotId)
}
