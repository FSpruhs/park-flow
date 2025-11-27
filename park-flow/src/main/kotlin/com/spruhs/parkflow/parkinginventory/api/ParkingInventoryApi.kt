package com.spruhs.parkflow.parkinginventory.api

fun interface ParkingInventoryApi {
    suspend fun getParkingSpotTypes(parkingSpotId: ParkingSpotId): List<ParkingSpotType>
}
