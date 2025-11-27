package com.spruhs.parkflow.customeraccess.api

import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId

interface CustomerApi {
    suspend fun isPlateNumberRegistered(plateNumber: PlateNumber): Boolean

    suspend fun isParkingSpotRented(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ): Boolean
}
