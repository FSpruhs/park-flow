package com.spruhs.parkflow.parkinginventory.core.domain

import com.spruhs.parkflow.parkinginventory.api.GateType

data class ParkingInventoryProjection(
    val gates: MutableList<GateProjection> = mutableListOf(),
    val parkingSpots: MutableList<ParkingSpotProjection> = mutableListOf(),
)

data class GateProjection(
    val gateId: String,
    val name: String,
    val type: GateType,
    val state: ActivationState = ActivationState.ACTIVE,
)

data class ParkingSpotProjection(
    val parkingSpotId: String,
    val name: String,
    val types: List<String>,
    val state: ActivationState = ActivationState.ACTIVE,
    val price: String?,
)
