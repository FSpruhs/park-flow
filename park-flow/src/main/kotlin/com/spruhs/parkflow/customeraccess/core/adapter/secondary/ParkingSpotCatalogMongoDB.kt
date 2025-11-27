package com.spruhs.parkflow.customeraccess.core.adapter.secondary

import com.spruhs.parkflow.customeraccess.core.application.ParkingSpotCatalogRepositoryPort
import com.spruhs.parkflow.customeraccess.core.domain.ParkingSpotCatalogItem
import com.spruhs.parkflow.customeraccess.core.domain.ParkingSpotCatalogProjection
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

private const val PARKING_SPOT_CATALOG_ID = "parking-spot-catalog-singleton-id"

@Document("parking_spot_catalog")
data class ParkingSpotCatalogDocument(
    @Id
    val id: String = PARKING_SPOT_CATALOG_ID,
    val parkingSpotCatalogItems: List<ParkingSpotCatalogItem>,
)

@Service
class ParkingSpotCatalogRepositoryAdapter(
    private val repository: ParkingSpotCatalogRepository,
) : ParkingSpotCatalogRepositoryPort {
    override suspend fun getCatalog() =
        repository.findById(PARKING_SPOT_CATALOG_ID)
            .awaitSingleOrNull()
            ?.parkingSpotCatalogItems
            ?.let { ParkingSpotCatalogProjection(it.toMutableList()) }
            ?: ParkingSpotCatalogProjection(mutableListOf())

    override suspend fun save(catalog: ParkingSpotCatalogProjection) {
        repository.save(ParkingSpotCatalogDocument(parkingSpotCatalogItems = catalog.parkingSpotCatalogItems))
            .awaitSingle()
    }
}

@Repository
interface ParkingSpotCatalogRepository : ReactiveMongoRepository<ParkingSpotCatalogDocument, String>
