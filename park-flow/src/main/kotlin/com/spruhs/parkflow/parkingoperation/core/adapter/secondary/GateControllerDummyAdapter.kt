package com.spruhs.parkflow.parkingoperation.core.adapter.secondary

import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkingoperation.core.application.GateControllerPort
import com.spruhs.parkflow.parkingoperation.core.domain.GateResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class GateControllerDummyAdapter(private val webClient: WebClient) : GateControllerPort {
    private val log = getLogger(javaClass)

    override suspend fun showError(
        gateId: GateId,
        plateNumber: PlateNumber,
        error: GateResponse.Error,
    ) {
        log.warn("Error $error at gate ${gateId.value}")
        gateError(gateId, plateNumber)
    }

    override suspend fun showProvidedParkingSpot(
        gateId: GateId,
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) {
        log.info("Show parking spot ${parkingSpotId.value} at gate ${gateId.value}")
        openEntranceGate(gateId, plateNumber, parkingSpotId)
    }

    override suspend fun openGate(
        gateId: GateId,
        plateNumber: PlateNumber,
    ) {
        log.info("Open gate ${gateId.value}")
        openExitGate(gateId, plateNumber)
    }

    suspend fun openEntranceGate(
        gateId: GateId,
        plateNumber: PlateNumber,
        parkingSpotId: ParkingSpotId,
    ) = webClient.post()
        .uri("/gate/${gateId.value}/entrance/${plateNumber.value}/${parkingSpotId.value}")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun openExitGate(
        gateId: GateId,
        plateNumber: PlateNumber,
    ) = webClient.post()
        .uri("/gate/${gateId.value}/exit/${plateNumber.value}")
        .retrieve()
        .awaitBody<Unit>()

    suspend fun gateError(
        gateId: GateId,
        plateNumber: PlateNumber,
    ) = webClient.post()
        .uri("/gate/${gateId.value}/error/${plateNumber.value}")
        .retrieve()
        .awaitBody<Unit>()
}
