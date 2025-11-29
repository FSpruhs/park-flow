package com.spruhs.parkflow.parkingoperation.core.infrastructure.secondary

import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkingoperation.api.ParkingSpotReprovidedEvent
import com.spruhs.parkflow.parkingoperation.core.application.CustomerNotificationPort
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class CustomerNotificationDummyAdapter(private val webClient: WebClient) : CustomerNotificationPort {
    private val log = getLogger(javaClass)

    override suspend fun notify(event: ParkingSpotReprovidedEvent) {
        log.info(
            "Your vehicle with plate number ${event.plateNumber.value} has been assigned a new parking spot." +
                " Please park at spot ${event.parkingSpotId.value}.",
        )
        notifyCustomer(event.plateNumber, event.parkingSpotId)
    }

    private suspend fun notifyCustomer(
        plateNumber: PlateNumber,
        parkingSpotId: ParkingSpotId,
    ) = webClient.post()
        .uri("/notification/reprovide/${plateNumber.value}/${parkingSpotId.value}")
        .retrieve()
        .awaitBody<Unit>()
}
