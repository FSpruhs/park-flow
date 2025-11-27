package com.spruhs.parkflow.customeraccess.core.domain

import java.time.LocalDate
import kotlin.collections.plus

data class CustomerListProjection(val customers: List<CustomerProjection>)

data class CustomerProjection(
    val customerId: String,
    val vehicles: List<VehicleProjection> = emptyList(),
    val paymentMethodId: String,
)

data class VehicleProjection(
    val plateNumber: String,
    val rentedParkingSpotId: String? = null,
    val rentedFrom: String? = null,
    val rentedTo: String? = null,
)

fun CustomerProjection.updatePaymentMethod(newPaymentMethodId: String) =
    this.copy(paymentMethodId = newPaymentMethodId)

fun CustomerProjection.addVehicle(plateNumber: String) =
    this.copy(vehicles = this.vehicles + VehicleProjection(plateNumber))

fun CustomerProjection.removeVehicle(plateNumber: String) =
    this.copy(vehicles = this.vehicles.filter { it.plateNumber != plateNumber })

fun CustomerProjection.rentParkingSpot(parkingSpotId: String, plateNumber: String, rentedAt: LocalDate): CustomerProjection {
    val updatedVehicles = vehicles.map { vehicle ->
        if (vehicle.plateNumber == plateNumber) {
            vehicle.copy(
                rentedParkingSpotId = parkingSpotId,
                rentedFrom = rentedAt.toString(),
            )
        } else {
            vehicle
        }
    }

    return copy(vehicles = updatedVehicles)
}

fun CustomerProjection.cancelParkingSpotRent(parkingSpotId: String, endOfRental: LocalDate): CustomerProjection {
    val updateVehicles = vehicles.map { vehicle ->
        if (vehicle.rentedParkingSpotId == parkingSpotId) {
            vehicle.copy(
                rentedTo = endOfRental.toString(),
            )
        } else {
            vehicle
        }
    }

    return copy(vehicles = updateVehicles)
}
