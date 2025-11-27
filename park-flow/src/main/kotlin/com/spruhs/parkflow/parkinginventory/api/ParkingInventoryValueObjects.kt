package com.spruhs.parkflow.parkinginventory.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.math.BigDecimal

@JvmInline
value class Price(val value: BigDecimal) {
    init {
        require(value.scale() <= 2) { "Price can have max 2 decimal places" }
        require(value >= BigDecimal.ZERO) { "Price must not be negative" }
    }
}

@JvmInline
value class ParkingSpotId(val value: String) {
    init {
        require(value.isNotBlank()) { "Parking spot id cannot be blank" }
    }
}

@JvmInline
value class GateId(val value: String) {
    init {
        require(value.isNotBlank()) { "Gate id cannot be blank" }
    }
}

enum class GateType {
    ENTRANCE,
    EXIT,
}

sealed interface ParkingSpotType {
    object Disabled : ParkingSpotType

    object Rentable : ParkingSpotType

    object Electric : ParkingSpotType

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromString(value: String): ParkingSpotType =
            when (value.trim().uppercase()) {
                "DISABLED" -> Disabled
                "RENTABLE" -> Rentable
                "ELECTRIC" -> Electric
                else -> throw IllegalArgumentException("Unknown ParkingSpotType: $value")
            }
    }

    @JsonValue
    fun toValue(): String =
        when (this) {
            Disabled -> "DISABLED"
            Rentable -> "RENTABLE"
            Electric -> "ELECTRIC"
        }
}
