package com.spruhs.parkflow.parkinginventory.core.domain

import com.spruhs.parkflow.parkinginventory.api.GateType
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType

class ParkingInventoryProjection(
    val gates: MutableList<GateProjection> = mutableListOf(),
    val parkingSpots: MutableList<ParkingSpotProjection> = mutableListOf(),
) {
    fun addGate(gate: GateProjection) = gates.add(gate)

    fun removeGate(gateId: String) = gates.removeIf { it.gateId == gateId }

    fun activateGate(gateId: String) = updateGate(gateId) { it.copy(state = ActivationState.ACTIVE) }

    fun deactivateGate(gateId: String) = updateGate(gateId) { it.copy(state = ActivationState.INACTIVE) }

    fun addParkingSpot(parkingSpot: ParkingSpotProjection) = parkingSpots.add(parkingSpot)

    fun removeParkingSpot(parkingSpotId: String) = parkingSpots.removeIf { it.parkingSpotId == parkingSpotId }

    fun activateParkingSpot(parkingSpotId: String) =
        updateParkingSpot(parkingSpotId) { it.copy(state = ActivationState.ACTIVE) }

    fun deactivateParkingSpot(parkingSpotId: String) =
        updateParkingSpot(parkingSpotId) { it.copy(state = ActivationState.INACTIVE) }

    fun renameParkingSpot(
        parkingSpotId: String,
        newName: ParkingSpotName,
    ) = updateParkingSpot(parkingSpotId) { it.copy(name = newName.value) }

    fun addParkingSpotType(
        parkingSpotId: String,
        types: Set<ParkingSpotType>,
        price: String? = null,
    ) = updateParkingSpot(parkingSpotId) { spot ->
        spot.copy(
            types = spot.types + types,
            price = if (ParkingSpotType.Rentable in types) price else spot.price,
        )
    }

    fun removeParkingSpotType(
        parkingSpotId: String,
        types: Set<ParkingSpotType>,
    ) = updateParkingSpot(parkingSpotId) {
        it.copy(
            types = it.types - types,
            price = if (ParkingSpotType.Rentable in types) null else it.price,
        )
    }

    fun existsGateName(name: GateName) = gates.any { it.name == name.value }

    fun existsParkingSpotName(name: ParkingSpotName) = parkingSpots.any { it.name == name.value }

    private fun updateGate(
        gateId: String,
        transform: (GateProjection) -> GateProjection,
    ) {
        val idx = gates.indexOfFirst { it.gateId == gateId }
        if (idx >= 0) {
            gates[idx] = transform(gates[idx])
        }
    }

    private fun updateParkingSpot(
        parkingSpotId: String,
        transform: (ParkingSpotProjection) -> ParkingSpotProjection,
    ) {
        val idx = parkingSpots.indexOfFirst { it.parkingSpotId == parkingSpotId }
        if (idx >= 0) {
            parkingSpots[idx] = transform(parkingSpots[idx])
        }
    }
}

data class GateProjection(
    val gateId: String,
    val name: String,
    val type: GateType,
    val state: ActivationState = ActivationState.ACTIVE,
)

data class ParkingSpotProjection(
    val parkingSpotId: String,
    val name: String,
    val types: List<ParkingSpotType>,
    val state: ActivationState = ActivationState.ACTIVE,
    val price: String?,
)
