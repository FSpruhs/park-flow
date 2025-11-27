package com.spruhs.parkflow.customeraccess.core.adapter.primary

import com.spruhs.parkflow.customeraccess.core.application.CustomerListQueryPort
import com.spruhs.parkflow.customeraccess.core.domain.CustomerListProjection
import com.spruhs.parkflow.customeraccess.core.domain.CustomerProjection
import com.spruhs.parkflow.customeraccess.core.domain.VehicleProjection
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/customer-access/customer-list")
class CustomerListRestAdapter(private val queryPort: CustomerListQueryPort) {
    @GetMapping
    suspend fun getCustomerList(): CustomerListMessage = queryPort.getCustomerList().toMessage()
}

data class CustomerListMessage(val customers: List<CustomerMessage>)

data class CustomerMessage(
    val id: String,
    val paymentMethodId: String,
    val vehicles: List<VehicleMessage>,
)

data class VehicleMessage(
    val plateNumber: String,
    val rentedParkingSpotId: String?,
    val rentedFrom: String?,
    val rentedTo: String?,
)

private fun CustomerProjection.toMessage() =
    CustomerMessage(
        id = this.customerId,
        paymentMethodId = this.paymentMethodId,
        vehicles = this.vehicles.map { it.toMessage() },
    )

private fun VehicleProjection.toMessage() =
    VehicleMessage(
        plateNumber = this.plateNumber,
        rentedParkingSpotId = this.rentedParkingSpotId,
        rentedFrom = this.rentedFrom,
        rentedTo = this.rentedTo,
    )

private fun CustomerListProjection.toMessage() =
    CustomerListMessage(
        customers = this.customers.map { it.toMessage() },
    )
