package com.spruhs.parkflow.customeraccess.core.domain

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
