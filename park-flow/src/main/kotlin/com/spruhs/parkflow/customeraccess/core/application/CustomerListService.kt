package com.spruhs.parkflow.customeraccess.core.application

import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerPaymentMethodChangedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleRemovedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.customeraccess.core.domain.CustomerId
import com.spruhs.parkflow.customeraccess.core.domain.CustomerListProjection
import com.spruhs.parkflow.customeraccess.core.domain.CustomerNotFoundException
import com.spruhs.parkflow.customeraccess.core.domain.CustomerProjection
import com.spruhs.parkflow.customeraccess.core.domain.VehicleProjection
import com.spruhs.parkflow.customeraccess.core.domain.addVehicle
import com.spruhs.parkflow.customeraccess.core.domain.cancelParkingSpotRent
import com.spruhs.parkflow.customeraccess.core.domain.removeVehicle
import com.spruhs.parkflow.customeraccess.core.domain.rentParkingSpot
import com.spruhs.parkflow.customeraccess.core.domain.updatePaymentMethod
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CustomerListService(private val repository: CustomerListRepositoryPort) {
    private val mutex = Mutex()

    suspend fun getCustomerList(): CustomerListProjection = repository.findAll()

    suspend fun handleCustomerCreated(event: CustomerCreatedEvent) {
        CustomerProjection(
            customerId = event.aggregateId,
            paymentMethodId = event.paymentMethodId.value,
            vehicles = listOf(VehicleProjection(event.plateNumber.value)),
        ).let { repository.save(it) }
    }

    suspend fun handlePaymentMethodChanged(event: CustomerPaymentMethodChangedEvent) =
        handle(event.aggregateId) { it.updatePaymentMethod(event.paymentMethodId.value) }

    suspend fun handleVehicleAdded(event: CustomerVehicleAddedEvent) =
        handle(event.aggregateId) { it.addVehicle(event.plateNumber.value) }

    suspend fun handleVehicleRemoved(event: CustomerVehicleRemovedEvent) =
        handle(event.aggregateId) { it.removeVehicle(event.plateNumber.value) }

    suspend fun handleParkingSpotRented(event: CustomerParkingSpotRentedEvent) =
        handle(event.aggregateId) {
            it.rentParkingSpot(event.parkingSpotId.value, event.plateNumber.value, event.rentedAt)
        }

    suspend fun handleParkingSpotCanceled(event: CustomerParkingSpotCanceledEvent) =
        handle(event.aggregateId) { it.cancelParkingSpotRent(event.parkingSpotId.value, event.endOfRental) }

    private suspend inline fun handle(
        customerId: String,
        block: (CustomerProjection) -> CustomerProjection,
    ) {
        mutex.withLock {
            loadCustomer(customerId).also { customer ->
                block(customer).also { repository.save(it) }
            }
        }
    }

    private suspend fun loadCustomer(customerId: String): CustomerProjection =
        repository.findById(customerId) ?: throw CustomerNotFoundException(CustomerId(customerId))

    suspend fun isParkingSpotRented(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ): Boolean {
        val vehicle =
            repository
                .findByPlateNumber(plateNumber.value)
                ?.vehicles
                ?.find { it.rentedParkingSpotId == parkingSpotId.value }
                ?: return false

        val rentedFrom = vehicle.rentedFrom?.let(LocalDate::parse) ?: return false
        val rentedTo = vehicle.rentedTo?.let(LocalDate::parse)

        val today = LocalDate.now()

        if (today.isBefore(rentedFrom)) return false
        if (rentedTo == null) return true

        return !today.isAfter(rentedTo)
    }
}

interface CustomerListRepositoryPort {
    suspend fun save(customer: CustomerProjection)

    suspend fun findByPlateNumber(plateNumber: String): CustomerProjection?

    suspend fun findById(id: String): CustomerProjection?

    suspend fun findAll(): CustomerListProjection
}
