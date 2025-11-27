package com.spruhs.parkflow.customeraccess.core.infrastructure.primary

import com.spruhs.parkflow.customeraccess.core.application.ParkingSpotCatalogQueryPort
import com.spruhs.parkflow.customeraccess.core.domain.ParkingSpotCatalogItem
import com.spruhs.parkflow.customeraccess.core.domain.ParkingSpotCatalogProjection
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/v1/customer-access/parking-spot-catalog")
class ParkingSpotCatalogRestAdapter(private val queryPort: ParkingSpotCatalogQueryPort) {
    @GetMapping
    suspend fun parkingSpotCatalog(): ParkingSpotCatalogMessage = queryPort.getCatalog().toMessage()
}

data class ParkingSpotCatalogMessage(
    val parkingSpots: List<ParkingSpotCatalogItemMessage>,
)

data class ParkingSpotCatalogItemMessage(
    val parkingSpotId: String,
    val price: String,
    val isElectrical: Boolean,
    val isActive: Boolean,
    val availableFrom: String?,
)

private fun ParkingSpotCatalogProjection.toMessage() =
    ParkingSpotCatalogMessage(
        parkingSpots = parkingSpotCatalogItems.map { it.toMessage() },
    )

private fun ParkingSpotCatalogItem.toMessage() =
    ParkingSpotCatalogItemMessage(
        parkingSpotId = this.parkingSpotId,
        price = this.price,
        isElectrical = this.isElectrical,
        isActive = this.isActive,
        availableFrom = this.availableFrom?.format(DateTimeFormatter.ISO_LOCAL_DATE),
    )
