package com.spruhs.parkflow.customeraccess.core.domain

import com.spruhs.parkflow.common.es.AggregateRoot
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.common.helper.generateId
import com.spruhs.parkflow.customeraccess.api.CustomerCreatedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerPaymentMethodChangedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleAddedEvent
import com.spruhs.parkflow.customeraccess.api.CustomerVehicleRemovedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CustomerAggregate(override val aggregateId: String) : AggregateRoot(aggregateId, TYPE) {
    var defaultPayment: PaymentMethodId = PaymentMethodId("EMPTY")
    val vehicles: MutableList<Vehicle> = mutableListOf()
    val rentedParkingSpots: MutableList<RentedParkingSpot> = mutableListOf()

    override fun whenEvent(event: BaseEvent) {
        when (event) {
            is CustomerCreatedEvent -> handleCreatedEvent(event)
            is CustomerPaymentMethodChangedEvent -> this.defaultPayment = event.paymentMethodId
            is CustomerVehicleAddedEvent -> vehicles.add(Vehicle(event.plateNumber))
            is CustomerVehicleRemovedEvent -> vehicles.remove(Vehicle(event.plateNumber))
            is CustomerParkingSpotRentedEvent -> handleRentedEvent(event)
            is CustomerParkingSpotCanceledEvent -> handleCanceledEvent(event)

            else -> UnknownEventTypeException(event)
        }
    }

    private fun handleCanceledEvent(event: CustomerParkingSpotCanceledEvent) {
        rentedParkingSpots.removeIf { it.parkingSpotId == event.parkingSpotId }
    }

    private fun handleRentedEvent(event: CustomerParkingSpotRentedEvent) {
        rentedParkingSpots.add(
            RentedParkingSpot(
                parkingSpotId = event.parkingSpotId,
                plateNumber = event.plateNumber,
                start = event.rentedAt,
            ),
        )
    }

    private fun handleCreatedEvent(event: CustomerCreatedEvent) {
        this.defaultPayment = event.paymentMethodId
        this.vehicles.add(Vehicle(event.plateNumber))
    }

    fun changePaymentMethod(paymentMethodId: PaymentMethodId) {
        if (paymentMethodId == this.defaultPayment) return

        apply(CustomerPaymentMethodChangedEvent(this.aggregateId, paymentMethodId))
    }

    fun cancelParkingSpot(parkingSpotId: ParkingSpotId) {
        val rentedParkingSpot = rentedParkingSpots.find { it.parkingSpotId == parkingSpotId }
        if (rentedParkingSpot == null) return

        apply(
            CustomerParkingSpotCanceledEvent(
                this.aggregateId,
                parkingSpotId,
                calculateEndOfRental(rentedParkingSpot.start),
            ),
        )
    }

    private fun calculateEndOfRental(rentalStart: LocalDate): LocalDate {
        val today = LocalDate.now()
        require(rentalStart.minusDays(1).isBefore(today)) {
            "Rental start date $rentalStart must be before today $today"
        }

        val monthsBetween = ChronoUnit.MONTHS.between(rentalStart, today)
        return rentalStart.plusMonths(monthsBetween + 1)
    }

    fun rentParkingSpot(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) {
        findParkingSpot(plateNumber)?.also { throw VehicleAlreadyRentedParkingSpotException(plateNumber) }

        findParkingSpot(parkingSpotId)
            ?.also { throw IllegalStateException("Parking spot $parkingSpotId is already rented") }

        apply(CustomerParkingSpotRentedEvent(this.aggregateId, parkingSpotId, plateNumber, LocalDate.now()))
    }

    fun removeVehicle(plateNumber: PlateNumber) {
        findVehicle(plateNumber) ?: return

        apply(CustomerVehicleRemovedEvent(this.aggregateId, plateNumber))

        val rentedSpot = findParkingSpot(plateNumber) ?: return
        apply(
            CustomerParkingSpotCanceledEvent(
                this.aggregateId,
                rentedSpot.parkingSpotId,
                calculateEndOfRental(rentedSpot.start),
            ),
        )
    }

    fun addVehicle(plateNumber: PlateNumber) {
        findVehicle(plateNumber)?.also { throw VehicleAlreadyExistsException(plateNumber) }

        apply(CustomerVehicleAddedEvent(this.aggregateId, plateNumber))
    }

    private fun findVehicle(plateNumber: PlateNumber): Vehicle? = vehicles.find { it.plateNumber == plateNumber }

    private fun findParkingSpot(parkingSpotId: ParkingSpotId): RentedParkingSpot? =
        rentedParkingSpots.find { it.parkingSpotId == parkingSpotId }

    private fun findParkingSpot(plateNumber: PlateNumber): RentedParkingSpot? =
        rentedParkingSpots.find { it.plateNumber == plateNumber }

    companion object {
        const val TYPE = "Customer"

        fun create(
            paymentMethodId: PaymentMethodId,
            vehiclePlateNumber: PlateNumber,
        ): CustomerAggregate =
            CustomerAggregate(generateId()).also {
                it.apply(
                    CustomerCreatedEvent(
                        it.aggregateId,
                        paymentMethodId,
                        vehiclePlateNumber,
                    ),
                )
            }
    }
}

@JvmInline
value class PaymentMethodId(val value: String) {
    init {
        require(!value.isBlank()) { "Identifier cannot be blank" }
    }
}

data class Vehicle(
    val plateNumber: PlateNumber,
)

data class RentedParkingSpot(
    val parkingSpotId: ParkingSpotId,
    val plateNumber: PlateNumber,
    val start: LocalDate,
)

@JvmInline
value class CustomerId(val value: String) {
    init {
        require(value.isNotBlank()) { "Identifier cannot be blank" }
    }
}

data class CustomerNotFoundException(val id: CustomerId) : RuntimeException("Could not find customer with id: $id")

data class VehicleAlreadyRentedParkingSpotException(val plateNumber: PlateNumber) : RuntimeException(
    "Vehicle with plate number ${plateNumber.value} already rented parking spot",
)

data class VehicleAlreadyExistsException(val plateNumber: PlateNumber) : RuntimeException(
    "Vehicle with plate number ${plateNumber.value} already exists",
)
