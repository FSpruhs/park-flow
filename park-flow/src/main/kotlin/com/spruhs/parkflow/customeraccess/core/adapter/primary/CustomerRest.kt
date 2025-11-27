package com.spruhs.parkflow.customeraccess.core.adapter.primary

import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.customeraccess.core.application.CreateCustomerCommand
import com.spruhs.parkflow.customeraccess.core.application.CustomerCommandPort
import com.spruhs.parkflow.customeraccess.core.application.RentParkingSpotCommand
import com.spruhs.parkflow.customeraccess.core.domain.CustomerId
import com.spruhs.parkflow.customeraccess.core.domain.CustomerNotFoundException
import com.spruhs.parkflow.customeraccess.core.domain.PaymentMethodId
import com.spruhs.parkflow.customeraccess.core.domain.VehicleAlreadyExistsException
import com.spruhs.parkflow.customeraccess.core.domain.VehicleAlreadyRentedParkingSpotException
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/customer-access/customer")
class CustomerRestAdapter(private val commandPort: CustomerCommandPort) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createCustomer(
        @RequestBody body: CreateCustomerRequest,
    ): String = commandPort.create(body.toCommand())

    @PostMapping("/{customerId}/payment-method")
    suspend fun changePaymentMethod(
        @PathVariable customerId: String,
        @RequestParam method: String,
    ) {
        commandPort.changePaymentMethod(CustomerId(customerId), PaymentMethodId(method))
    }

    @PostMapping("/{customerId}/vehicle")
    suspend fun addVehicle(
        @PathVariable customerId: String,
        @RequestParam plate: String,
    ) {
        commandPort.addVehicle(CustomerId(customerId), PlateNumber(plate))
    }

    @DeleteMapping("/{customerId}/vehicle")
    suspend fun removeVehicle(
        @PathVariable customerId: String,
        @RequestParam plate: String,
    ) {
        commandPort.removeVehicle(CustomerId(customerId), PlateNumber(plate))
    }

    @PostMapping("/{customerId}/rented-parking-spots")
    suspend fun rentParkingSpot(
        @PathVariable customerId: String,
        @RequestBody body: RentParkingSpotRequest,
    ) {
        commandPort.rentParkingSpot(body.toCommand(customerId))
    }

    @DeleteMapping("/{customerId}/rented-parking-spots")
    suspend fun cancelParkingSpot(
        @PathVariable customerId: String,
        @RequestParam parkingSpot: String,
    ) {
        commandPort.cancelParkingSpot(CustomerId(customerId), ParkingSpotId(parkingSpot))
    }
}

@ControllerAdvice
class CustomerExceptionHandler {
    private val log = getLogger(javaClass)

    @ExceptionHandler
    fun handleCustomerNotFoundException(ex: CustomerNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
            .also { log.error(ex.message, it) }

    @ExceptionHandler
    fun handleVehicleAlreadyRentedParkingSpotException(ex: VehicleAlreadyRentedParkingSpotException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
            .also { log.error(ex.message, it) }

    @ExceptionHandler
    fun handleVehicleAlreadyExistsException(ex: VehicleAlreadyExistsException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
            .also { log.error(ex.message, it) }
}

data class CreateCustomerRequest(
    val paymentMethodId: String,
    val vehiclePlateNumber: String,
)

data class RentParkingSpotRequest(
    val parkingSpotId: String,
    val vehiclePlateNumber: String,
)

private fun CreateCustomerRequest.toCommand() =
    CreateCustomerCommand(
        paymentMethodId = PaymentMethodId(this.paymentMethodId),
        vehiclePlateNumber = PlateNumber(this.vehiclePlateNumber),
    )

private fun RentParkingSpotRequest.toCommand(customerId: String) =
    RentParkingSpotCommand(
        customerId = CustomerId(customerId),
        plateNumber = PlateNumber(this.vehiclePlateNumber),
        parkingSpotId = ParkingSpotId(this.parkingSpotId),
    )
