package com.spruhs.parkflow.parkinginventory.core.infrastructure.primary

import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import com.spruhs.parkflow.parkinginventory.api.Price
import com.spruhs.parkflow.parkinginventory.core.application.AddParkingSpotTypeCommand
import com.spruhs.parkflow.parkinginventory.core.application.CreateParkingSpotCommand
import com.spruhs.parkflow.parkinginventory.core.application.ParkingSpotCommandPort
import com.spruhs.parkflow.parkinginventory.core.domain.ActivationState
import com.spruhs.parkflow.parkinginventory.core.domain.DisabledParkingSpotsNotRentableException
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotName
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotNotFoundException
import com.spruhs.parkflow.parkinginventory.core.domain.RentableParkingSpotWithoutPriceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/parking-inventory/parking-spots")
class ParkingSpotRestAdapter(private val commandPort: ParkingSpotCommandPort) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createParkingSpot(
        @RequestBody body: CreateParkingSpotRequest,
    ) = commandPort.create(body.toCommand())

    @PutMapping("/{parkingSpotId}/name")
    suspend fun renameParkingSpot(
        @PathVariable parkingSpotId: String,
        @RequestParam name: String,
    ) = commandPort.rename(ParkingSpotId(parkingSpotId), ParkingSpotName(name))

    @PostMapping("/{parkingSpotId}/types")
    suspend fun addParkingSpotTypes(
        @PathVariable parkingSpotId: String,
        @RequestBody body: ParkingSpotTypesRequest,
    ) = commandPort.addTypes(body.toCommand(parkingSpotId))

    @PutMapping("/{parkingSpotId}/types")
    suspend fun removeParkingSpotTypes(
        @PathVariable parkingSpotId: String,
        @RequestBody body: ParkingSpotTypesRequest,
    ) = commandPort.removeTypes(ParkingSpotId(parkingSpotId), body.toTypes())

    @PostMapping("/{parkingSpotId}/activation-state")
    suspend fun updateActivationState(
        @PathVariable parkingSpotId: String,
        @RequestParam state: String,
    ) = when (ActivationState.valueOf(state)) {
        ActivationState.ACTIVE -> commandPort.activate(ParkingSpotId(parkingSpotId))
        ActivationState.INACTIVE -> commandPort.deactivate(ParkingSpotId(parkingSpotId))
    }

    @DeleteMapping("/{parkingSpotId}")
    suspend fun removeParkingSpot(
        @PathVariable parkingSpotId: String,
    ) {
        commandPort.remove(ParkingSpotId(parkingSpotId))
    }
}

@ControllerAdvice
class ParkingSpotExceptionHandler {
    private val log = getLogger(javaClass)

    @ExceptionHandler
    fun handleParkingSpotNotFoundException(ex: ParkingSpotNotFoundException): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
            .also { log.error(ex.message, it) }

    @ExceptionHandler
    fun handleDisabledParkingSpotsNotRentableException(
        ex: DisabledParkingSpotsNotRentableException,
    ): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
            .also { log.error(ex.message, it) }

    @ExceptionHandler
    fun handleRentableParkingSpotWithoutPriceException(
        ex: RentableParkingSpotWithoutPriceException,
    ): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
            .also { log.error(ex.message, it) }
}

data class CreateParkingSpotRequest(
    val parkingSpotName: String,
    val parkingSpotTypes: List<String>,
    val price: String? = null,
)

data class ParkingSpotTypesRequest(
    val types: List<String>,
    val price: String? = null,
)

private fun CreateParkingSpotRequest.toCommand() =
    CreateParkingSpotCommand(
        parkingSpotName = ParkingSpotName(parkingSpotName),
        parkingSpotTypes = parkingSpotTypes.map { ParkingSpotType.fromString(it) }.toSet(),
        price = price?.let { Price(BigDecimal(price)) },
    )

private fun ParkingSpotTypesRequest.toTypes() = types.map { ParkingSpotType.fromString(it) }.toSet()

private fun ParkingSpotTypesRequest.toCommand(parkingSpotId: String) =
    AddParkingSpotTypeCommand(
        parkingSpotId = ParkingSpotId(parkingSpotId),
        types = this.toTypes(),
        price = price?.let { Price(BigDecimal(price)) },
    )
