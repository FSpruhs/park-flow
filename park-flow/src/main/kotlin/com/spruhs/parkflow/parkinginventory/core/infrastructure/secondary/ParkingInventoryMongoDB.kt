package com.spruhs.parkflow.parkinginventory.core.infrastructure.secondary

import com.spruhs.parkflow.parkinginventory.core.application.ParkingInventoryRepositoryPort
import com.spruhs.parkflow.parkinginventory.core.domain.GateProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingInventoryProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotProjection
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

private const val PARKING_INVENTORY_ID = "paring-inventory-singleton-id"

@Document("parking_inventory")
data class ParkingInventoryDocument(
    @Id
    val id: String = PARKING_INVENTORY_ID,
    val parkingSpots: List<ParkingSpotProjection>,
    val gates: List<GateProjection>,
)

@Service
class ParkingInventoryRepositoryAdapter(
    private val repository: ParkingInventoryRepository,
) : ParkingInventoryRepositoryPort {
    override suspend fun getInventory() =
        repository.findById(PARKING_INVENTORY_ID)
            .awaitSingleOrNull()
            ?.toProjection()
            ?: ParkingInventoryProjection()

    override suspend fun save(inventoryProjection: ParkingInventoryProjection) {
        repository.save(inventoryProjection.toDocument()).awaitSingle()
    }
}

@Repository
interface ParkingInventoryRepository : ReactiveMongoRepository<ParkingInventoryDocument, String>

private fun ParkingInventoryProjection.toDocument() =
    ParkingInventoryDocument(
        gates = this.gates,
        parkingSpots = this.parkingSpots,
    )

private fun ParkingInventoryDocument.toProjection() =
    ParkingInventoryProjection(
        gates = this.gates.toMutableList(),
        parkingSpots = this.parkingSpots.toMutableList(),
    )
