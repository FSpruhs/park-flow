package com.spruhs.parkflow.customeraccess.core.application

import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerPaymentMethodChangedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleRemovedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.customeraccess.core.domain.CustomerListProjection
import com.spruhs.parkflow.customeraccess.core.domain.CustomerProjection
import com.spruhs.parkflow.customeraccess.core.domain.VehicleProjection
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
        handle(event.aggregateId) { it.copy(paymentMethodId = event.paymentMethodId.value) }

    suspend fun handleVehicleAdded(event: CustomerVehicleAddedEvent) {
        handle(event.aggregateId) {
            it.copy(vehicles = it.vehicles + VehicleProjection(event.plateNumber.value))
        }
    }

    suspend fun handleVehicleRemoved(event: CustomerVehicleRemovedEvent) {
        handle(event.aggregateId) { customer ->
            customer.copy(vehicles = customer.vehicles.filter { it.plateNumber != event.plateNumber.value })
        }
    }

    suspend fun handleParkingSpotRented(event: CustomerParkingSpotRentedEvent) {
        handle(event.aggregateId) { customer ->
            val vehicle = customer.vehicles.find { it.plateNumber == event.plateNumber.value } ?: return

            customer.copy(
                vehicles =
                    customer.vehicles - vehicle +
                        VehicleProjection(
                            plateNumber = event.plateNumber.value,
                            rentedParkingSpotId = event.parkingSpotId.value,
                            rentedFrom = event.rentedAt.toString(),
                        ),
            )
        }
    }

    suspend fun handleParkingSpotCanceled(event: CustomerParkingSpotCanceledEvent) {
        handle(event.aggregateId) { customer ->
            val vehicle = customer.vehicles.find { it.rentedParkingSpotId == event.parkingSpotId.value } ?: return
            customer.copy(
                vehicles =
                    customer.vehicles - vehicle +
                        VehicleProjection(
                            plateNumber = vehicle.plateNumber,
                            rentedParkingSpotId = event.parkingSpotId.value,
                            rentedFrom = vehicle.rentedFrom,
                            rentedTo = event.endOfRental.toString(),
                        ),
            )
        }
    }

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
        repository.findById(customerId) ?: throw IllegalArgumentException("Customer with id $customerId not found")

    suspend fun isParkingSpotRented(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ): Boolean {
        val vehicle =
            repository.findByPlateNumber(
                plateNumber.value,
            )?.vehicles?.find { it.rentedParkingSpotId == parkingSpotId.value }
        if (vehicle?.rentedParkingSpotId == null) return false
        if (vehicle.rentedParkingSpotId != parkingSpotId.value) return false
        if (vehicle.rentedFrom == null) return false
        if (!LocalDate.now().isBefore(LocalDate.parse(vehicle.rentedFrom))) {
            if (vehicle.rentedTo == null) return true
            if (LocalDate.now().isBefore(LocalDate.parse(vehicle.rentedTo)) ||
                LocalDate.now()
                    .equals(LocalDate.parse(vehicle.rentedTo))
            ) {
                return true
            }
        }
        return false
    }
}

interface CustomerListRepositoryPort {
    suspend fun save(customer: CustomerProjection)

    suspend fun findByPlateNumber(plateNumber: String): CustomerProjection?

    suspend fun findById(id: String): CustomerProjection?

    suspend fun findAll(): CustomerListProjection
}
