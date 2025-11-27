package com.spruhs.parkflow.parkinginventory.core.adapter.primary

import com.spruhs.parkflow.parkinginventory.api.GateType
import com.spruhs.parkflow.parkinginventory.core.application.ParkingInventoryQueryPort
import com.spruhs.parkflow.parkinginventory.core.domain.ActivationState
import com.spruhs.parkflow.parkinginventory.core.domain.GateProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingInventoryProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotProjection
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/parking-inventory")
class ParkingInventoryRestAdapter(private val queryPort: ParkingInventoryQueryPort) {
    @GetMapping
    suspend fun getInventory() = queryPort.getInventory().toMessage()
}

data class ParkingInventoryMessage(
    val gates: List<GateMessage>,
    val parkingSpots: List<ParkingSpotMessage>,
)

data class ParkingSpotMessage(
    val id: String,
    val name: String,
    val types: List<String>,
    val state: ActivationState,
    val price: String?,
)

data class GateMessage(
    val id: String,
    val name: String,
    val state: ActivationState,
    val type: GateType,
)

private fun ParkingInventoryProjection.toMessage() =
    ParkingInventoryMessage(
        gates = this.gates.map { it.toMessage() },
        parkingSpots = this.parkingSpots.map { it.toMessage() },
    )

private fun GateProjection.toMessage() =
    GateMessage(
        id = this.gateId,
        name = this.name,
        state = this.state,
        type = this.type,
    )

private fun ParkingSpotProjection.toMessage() =
    ParkingSpotMessage(
        id = this.parkingSpotId,
        name = this.name,
        types = this.types.map { it.toValue() },
        state = this.state,
        price = this.price,
    )
