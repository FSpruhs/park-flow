package com.spruhs.parkflow.customeraccess.core.application

import com.spruhs.parkflow.common.es.AggregateNotFoundException
import com.spruhs.parkflow.common.es.AggregateStore
import com.spruhs.parkflow.common.helper.KeyedMutex
import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.customeraccess.api.CustomerId
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.customeraccess.core.domain.CustomerAggregate
import com.spruhs.parkflow.customeraccess.core.domain.CustomerNotFoundException
import com.spruhs.parkflow.customeraccess.core.domain.PaymentMethodId
import com.spruhs.parkflow.customeraccess.core.domain.VehicleNotElectricalException
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import org.springframework.stereotype.Service

@Service
class CustomerCommandPort(
    private val aggregateStore: AggregateStore,
    private val plateNumberService: PlateNumberService,
    private val parkingSpotCatalogService: ParkingSpotCatalogService,
) {
    private val log = getLogger(javaClass)
    private val mutex = KeyedMutex<CustomerId>()

    suspend fun create(command: CreateCustomerCommand): String {
        reservePlateNumber(command.vehiclePlateNumber)
        return CustomerAggregate.create(command.paymentMethodId, command.vehiclePlateNumber)
            .also { aggregateStore.save(it) }
            .aggregateId
    }

    suspend fun changePaymentMethod(
        id: CustomerId,
        paymentMethodId: PaymentMethodId,
    ) = handle(id) { it.changePaymentMethod(paymentMethodId) }

    suspend fun addVehicle(
        id: CustomerId,
        plateNumber: PlateNumber,
    ) {
        reservePlateNumber(plateNumber)
        handle(id) { it.addVehicle(plateNumber) }
    }

    suspend fun removeVehicle(
        id: CustomerId,
        plateNumber: PlateNumber,
    ) = handle(id) { it.removeVehicle(plateNumber) }

    suspend fun rentParkingSpot(command: RentParkingSpotCommand) {
        validateElectricParkingSpot(command)
        reserveParkingSpot(command.parkingSpotId, command.plateNumber)

        handle(command.customerId) { it.rentParkingSpot(command.parkingSpotId, command.plateNumber) }
    }

    private suspend fun reserveParkingSpot(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) {
        parkingSpotCatalogService.reserve(parkingSpotId, plateNumber)
    }

    private suspend fun validateElectricParkingSpot(command: RentParkingSpotCommand) {
        require(
            !(parkingSpotCatalogService.isElectrical(command.parkingSpotId) && !command.plateNumber.isElectrical()),
        ) { throw VehicleNotElectricalException(command.plateNumber) }
    }

    suspend fun cancelParkingSpot(
        id: CustomerId,
        parkingSpotId: ParkingSpotId,
    ) = handle(id) { it.cancelParkingSpot(parkingSpotId) }

    private suspend inline fun handle(
        id: CustomerId,
        crossinline block: (CustomerAggregate) -> Unit,
    ) {
        mutex.withKeyLock(id) {
            loadCustomer(id).also {
                block(it)
                aggregateStore.save(it)
            }
        }
    }

    private suspend fun loadCustomer(id: CustomerId): CustomerAggregate =
        try {
            aggregateStore.load(id.value, CustomerAggregate::class.java)
        } catch (e: AggregateNotFoundException) {
            log.error(e.message)
            throw CustomerNotFoundException(id)
        }

    private suspend fun reservePlateNumber(plateNumber: PlateNumber) {
        plateNumberService.reservePlateNumber(plateNumber)
    }
}

data class CreateCustomerCommand(
    val paymentMethodId: PaymentMethodId,
    val vehiclePlateNumber: PlateNumber,
)

data class RentParkingSpotCommand(
    val customerId: CustomerId,
    val plateNumber: PlateNumber,
    val parkingSpotId: ParkingSpotId,
)
