package com.spruhs.parkflow.parkinginventory.core.infrastructure.primary

import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.GateType
import com.spruhs.parkflow.parkinginventory.core.application.CreateGateCommand
import com.spruhs.parkflow.parkinginventory.core.application.GateCommandPort
import com.spruhs.parkflow.parkinginventory.core.domain.ActivationState
import com.spruhs.parkflow.parkinginventory.core.domain.GateName
import com.spruhs.parkflow.parkinginventory.core.domain.GateNotFoundException
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
@RequestMapping("/api/v1/parking-inventory/gates")
class GateRestAdapter(private val commandPort: GateCommandPort) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createGate(
        @RequestBody body: CreateGateRequest,
    ) = commandPort.create(body.toCommand())

    @PostMapping("/{gateId}/activation-state")
    suspend fun updateActivationState(
        @PathVariable("gateId") gateId: String,
        @RequestParam state: String,
    ) = when (ActivationState.valueOf(state)) {
        ActivationState.ACTIVE -> commandPort.activate(GateId(gateId))
        ActivationState.INACTIVE -> commandPort.deactivate(GateId(gateId))
    }

    @DeleteMapping("/{gateId}")
    suspend fun removeGate(
        @PathVariable("gateId") gateId: String,
    ) = commandPort.remove(GateId(gateId))
}

@ControllerAdvice
class GateExceptionHandler {
    private val log = getLogger(javaClass)

    @ExceptionHandler
    fun handleGateNotFoundException(ex: GateNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
            .also { log.error(ex.message, it) }
}

data class CreateGateRequest(
    val gateName: String,
    val gateType: String,
)

private fun CreateGateRequest.toCommand() =
    CreateGateCommand(
        gateName = GateName(this.gateName),
        gateType = GateType.valueOf(this.gateType),
    )
