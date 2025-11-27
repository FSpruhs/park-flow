package com.spruhs.parkflowsimulator.webclient

import com.spruhs.parkflowsimulator.scenario.CustomerInfo
import com.spruhs.parkflowsimulator.scenario.GateInfo
import com.spruhs.parkflowsimulator.scenario.ParkingSpotInfo
import com.spruhs.parkflowsimulator.scenario.VehicleHistoryReflection
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class ParkFlowWebClientService(private val webClient: WebClient) {

    suspend fun createParkingSpot(parkingSpot: ParkingSpotInfo) = webClient.post()
        .uri("/parking-inventory/parking-spots")
        .bodyValue(parkingSpot.toRequest())
        .retrieve()
        .awaitBody<String>()

    suspend fun renameParkingSpot(spotId: String, newName: String) = webClient.put()
        .uri("/parking-inventory/parking-spots/$spotId/name?name=$newName")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun createGate(gate: GateInfo) = webClient.post()
        .uri("/parking-inventory/gates")
        .bodyValue(gate.toRequest())
        .retrieve()
        .awaitBody<String>()

    suspend fun activateGate(gateId: String) = webClient.post()
        .uri("/parking-inventory/gates/$gateId/activation-state?state=ACTIVE")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun deactivateGate(gateId: String) = webClient.post()
        .uri("/parking-inventory/gates/$gateId/activation-state?state=INACTIVE")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun removeGate(gateId: String) = webClient.delete()
        .uri("/parking-inventory/gates/$gateId")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun addParkingSpotType(spotId: String, types: List<String>, price: String? = null) = webClient.post()
        .uri("/parking-inventory/parking-spots/$spotId/types")
        .bodyValue(ParkingSpotTypesRequest(types, price))
        .retrieve()
        .awaitBody<Unit>()

    suspend fun removeParkingSpotType(spotId: String, types: List<String>, price: String? = null) = webClient.put()
        .uri("/parking-inventory/parking-spots/$spotId/types")
        .bodyValue(ParkingSpotTypesRequest(types, price))
        .retrieve()
        .awaitBody<Unit>()

    suspend fun deactivateParkingSpot(spotId: String) = webClient.post()
        .uri("/parking-inventory/parking-spots/$spotId/activation-state?state=INACTIVE")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun activateParkingSpot(spotId: String) = webClient.post()
        .uri("/parking-inventory/parking-spots/$spotId/activation-state?state=ACTIVE")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun removeParkingSpot(spotId: String) = webClient.delete()
        .uri("/parking-inventory/parking-spots/$spotId")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun getParkingInventory() = webClient.get()
        .uri("/parking-inventory")
        .retrieve()
        .awaitBody<ParkingInventoryResponse>()

    suspend fun getParkingSpotCatalog() = webClient.get()
        .uri("/customer-access/parking-spot-catalog")
        .retrieve()
        .awaitBody<ParkingSpotCatalogResponse>()

    suspend fun createCustomer(info: CustomerInfo) = webClient.post()
        .uri("/customer-access/customer")
        .bodyValue(CreateCustomerRequest(info.paymentMethodId, info.plateNumber))
        .retrieve()
        .awaitBody<String>()

    suspend fun changePaymentMethod(customerId: String, paymentMethodId: String) = webClient.post()
        .uri("/customer-access/customer/$customerId/payment-method?method=$paymentMethodId")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun addVehicle(customerId: String, vehiclePlateNumber: String) = webClient.post()
        .uri("/customer-access/customer/$customerId/vehicle?plate=$vehiclePlateNumber")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun removeVehicle(customerId: String, vehiclePlateNumber: String) = webClient.delete()
        .uri("/customer-access/customer/$customerId/vehicle?plate=$vehiclePlateNumber")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun rentParkingSpot(
        customerId: String,
        vehiclePlateNumber: String,
        parkingSpotId: String,
    ) = webClient.post()
        .uri("/customer-access/customer/$customerId/rented-parking-spots")
        .bodyValue(RentParkingSpotRequest(parkingSpotId, vehiclePlateNumber))
        .retrieve()
        .awaitBody<Unit>()

    suspend fun cancelParkingSpot(customerId: String, parkingSpotId: String) = webClient.delete()
        .uri("/customer-access/customer/$customerId/rented-parking-spots?parkingSpot=$parkingSpotId")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun getCustomerList() = webClient.get()
        .uri("/customer-access/customer-list")
        .retrieve()
        .awaitBody<CustomerListResponse>()

    suspend fun getVehicleHistory(plateNumber: String) = webClient.get()
        .uri("/billing/vehicle-history/$plateNumber")
        .retrieve()
        .awaitBody<VehicleHistoryReflection>()
}

data class CreateParkingSpotRequest(
    val parkingSpotName: String,
    val parkingSpotTypes: List<String>,
    val price: String?
)

data class CreateCustomerRequest(
    val paymentMethodId: String,
    val vehiclePlateNumber: String
)

data class CreateGateRequest(
    val gateName: String,
    val gateType: String,
)

data class ParkingSpotTypesRequest(
    val types: List<String>,
    val price: String? = null
)

data class ParkingInventoryResponse(
    val gates: List<GateResponse>,
    val parkingSpots: List<ParkingSpotResponse>
)

data class ParkingSpotResponse(
    val id: String,
    val name: String,
    val types: List<String>,
    val state: String,
    val price: String?
)

data class GateResponse(
    val id: String,
    val name: String,
    val state: String,
    val type: String,
)

data class ParkingSpotCatalogResponse(
    val parkingSpots: List<ParkingSpotCatalogItemResponse>
)

data class ParkingSpotCatalogItemResponse(
    val parkingSpotId: String,
    val price: String,
    val isElectrical: Boolean,
    val isActive: Boolean,
    val availableFrom: String?,
)

data class RentParkingSpotRequest(
    val parkingSpotId: String,
    val vehiclePlateNumber: String
)

data class CustomerListResponse(val customers: List<CustomerResponse>)

data class CustomerResponse(
    val id: String,
    val paymentMethodId: String,
    val vehicles: List<VehicleResponse>,
)

data class VehicleResponse(
    val plateNumber: String,
    val rentedParkingSpotId: String?,
    val rentedFrom: String?,
    val rentedTo: String?,
)

private fun ParkingSpotInfo.toRequest() = CreateParkingSpotRequest(
    parkingSpotName = this.name,
    parkingSpotTypes = this.types,
    price = this.price
)

private fun GateInfo.toRequest() = CreateGateRequest(
    gateName = this.name,
    gateType = this.type
)
