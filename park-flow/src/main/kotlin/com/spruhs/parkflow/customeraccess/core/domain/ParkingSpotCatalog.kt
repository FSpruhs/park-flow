package com.spruhs.parkflow.customeraccess.core.domain

import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import com.spruhs.parkflow.parkinginventory.api.Price
import java.time.LocalDate

class ParkingSpotCatalogProjection(val parkingSpotCatalogItems: MutableList<ParkingSpotCatalogItem>) {
    fun addedParkingSpot(
        parkingSpotId: String,
        price: Price?,
        spotTypes: Set<ParkingSpotType>,
    ) {
        parkingSpotCatalogItems.add(
            ParkingSpotCatalogItem(
                parkingSpotId = parkingSpotId,
                price = price?.value?.toString() ?: "0.0",
                isElectrical = ParkingSpotType.Electric in spotTypes,
                isActive = true,
                availableFrom = LocalDate.now(),
            ),
        )
    }

    fun removedParkingSpot(parkingSpotId: String) {
        parkingSpotCatalogItems.removeIf { it.parkingSpotId == parkingSpotId }
    }

    fun activatedParkingSpot(parkingSpotId: String) {
        update(parkingSpotId) { it.copy(isActive = true) }
    }

    fun deactivatedParkingSpot(parkingSpotId: String) {
        update(parkingSpotId) { it.copy(isActive = false) }
    }

    fun cancelledParkingSpot(
        parkingSpotId: String,
        endOfRental: LocalDate,
    ) {
        update(parkingSpotId) { it.copy(availableFrom = endOfRental.plusDays(1)) }
    }

    fun rentedParkingSpot(parkingSpotId: String) {
        update(parkingSpotId) { it.copy(availableFrom = null) }
    }

    fun removedElectrical(parkingSpotId: String) {
        update(parkingSpotId) { it.copy(isElectrical = false) }
    }

    fun addElectrical(parkingSpotId: String) {
        update(parkingSpotId) { it.copy(isElectrical = true) }
    }

    private fun update(
        parkingSpotId: String,
        transform: (ParkingSpotCatalogItem) -> ParkingSpotCatalogItem,
    ) {
        val idx = parkingSpotCatalogItems.indexOfFirst { it.parkingSpotId == parkingSpotId }
        if (idx >= 0) {
            parkingSpotCatalogItems[idx] = transform(parkingSpotCatalogItems[idx])
        }
    }
}

data class ParkingSpotCatalogItem(
    val parkingSpotId: String,
    val price: String,
    val isElectrical: Boolean,
    val isActive: Boolean,
    val availableFrom: LocalDate?,
)
