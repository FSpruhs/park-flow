package com.spruhs.parkflowsimulator.scenario

import com.spruhs.parkflowsimulator.webclient.CustomerResponse
import com.spruhs.parkflowsimulator.webclient.GateResponse
import com.spruhs.parkflowsimulator.webclient.ParkingSpotCatalogItemResponse
import com.spruhs.parkflowsimulator.webclient.ParkingSpotResponse
import com.spruhs.parkflowsimulator.webclient.VehicleResponse

interface Validator<T> {
    val id: String
    fun validate(actual: T)
}

data class VehicleHistoryValidator(
    override val id: String,
    val expectedHistorySize: Int,
    val expectedHistoryTypes: Map<HistoryType, Int>,
    val expectedParkingSpotId: String? = null,
    val expectedPrice: String? = null
) : Validator<VehicleHistoryReflection> {
    override fun validate(actual: VehicleHistoryReflection) {
        require(actual.history.size == expectedHistorySize) { "Expected $expectedHistorySize history items but found ${actual.history.size}" }
        expectedHistoryTypes.forEach { (type, count) ->
            val actualCount = actual.history.count { it.type == type }
            require(actualCount == count) { "Expected $count entries of type $type but found $actualCount" }
        }
        expectedParkingSpotId?.let {
            require(actual.history.any { it.parkingSpotId == expectedParkingSpotId }) { "Expected history entry for $id for parking spot $expectedParkingSpotId" }
        }
        expectedPrice?.let {
            require(actual.history.any { it.amount == expectedPrice }) { "Expected history entry for $id with price $expectedPrice" }
        }

    }
}

data class ParkingSpotValidator(
    override val id: String,
    val expectedName: String? = null,
    val expectedState: String? = null,
    val expectedPrice: String? = null,
    val expectedTypes: Set<String>? = null,
) : Validator<ParkingSpotResponse> {
    override fun validate(actual: ParkingSpotResponse) {
        expectedName?.let { require(actual.name == expectedName) { "Parking spot name should be $expectedName but is ${actual.name}" } }
        expectedState?.let { require(actual.state == it) { "Parking spot state should be $it but is ${actual.state}" } }
        require(actual.price == expectedPrice) { "Parking spot price should be $expectedPrice but is ${actual.price}" }
        expectedTypes?.let {
            require(actual.types.toSet() == it) { "Parking spot types should be $it but are ${actual.types}" }
        }
    }
}

data class GateValidator(
    override val id: String,
    val expectedName: String? = null,
    val expectedType: String? = null,
    val expectedState: String? = null
) : Validator<GateResponse> {
    override fun validate(actual: GateResponse) {
        expectedName?.let { require(actual.name == expectedName) { "Gate name should be $expectedName but is ${actual.name}" } }
        expectedType?.let { require(actual.type == it) { "Gate type should be $it but is ${actual.type}" } }
        expectedState?.let { require(actual.state == it) { "Gate state should be $it but is ${actual.state}" } }
    }
}

data class ParkingSpotCatalogValidator(
    override val id: String,
    val expectedPrice: String,
    val expectedIsElectrical: Boolean,
    val expectedIsActive: Boolean,
    val expectedAvailableFrom: String?,
) : Validator<ParkingSpotCatalogItemResponse> {
    override fun validate(actual: ParkingSpotCatalogItemResponse) {
        require(actual.price == expectedPrice) { "Catalog item price should be $expectedPrice but is ${actual.price}" }
        require(actual.isElectrical == expectedIsElectrical) { "Catalog item isElectrical should be $expectedIsElectrical but is ${actual.isElectrical}" }
        require(actual.isActive == expectedIsActive) { "Catalog item isActive should be $expectedIsActive but is ${actual.isActive}" }
        require(actual.availableFrom == expectedAvailableFrom) { "Catalog item availableFrom should be $expectedAvailableFrom but is ${actual.availableFrom}" }
    }
}

data class CustomerValidator(
    override val id: String,
    val expectedPaymentMethodId: String,
    val expectedVehicles: List<VehicleResponse>
) : Validator<CustomerResponse> {
    override fun validate(actual: CustomerResponse) {
        require(actual.paymentMethodId == expectedPaymentMethodId) { "Customer paymentMethodId should be $expectedPaymentMethodId but is ${actual.paymentMethodId}" }
        require(actual.vehicles.size == expectedVehicles.size) { "Customer should have ${expectedVehicles.size} vehicles but has ${actual.vehicles.size}" }
        expectedVehicles.forEach { expectedVehicle ->
            val actualVehicle = actual.vehicles.find { it.plateNumber == expectedVehicle.plateNumber }
                ?: error("Vehicle with plateNumber ${expectedVehicle.plateNumber} not found")
            require(actualVehicle.rentedParkingSpotId == expectedVehicle.rentedParkingSpotId) {
                "Vehicle rentedParkingSpotId should be ${expectedVehicle.rentedParkingSpotId} but is ${actualVehicle.rentedParkingSpotId}"
            }
            require(actualVehicle.rentedFrom == expectedVehicle.rentedFrom) {
                "Vehicle rentedFrom should be ${expectedVehicle.rentedFrom} but is ${actualVehicle.rentedFrom}"
            }
            require(actualVehicle.rentedTo == expectedVehicle.rentedTo) {
                "Vehicle rentedTo should be ${expectedVehicle.rentedTo} but is ${actualVehicle.rentedTo}"
            }
        }
    }
}

fun <T> validateAll(
    actualList: List<T>,
    validators: List<Validator<T>>,
    findById: (T) -> String
) {
    validators.forEach { validator ->
        val actual = actualList.find { findById(it) == validator.id }
            ?: error("${validator.id} not found")
        validator.validate(actual)
    }
}


