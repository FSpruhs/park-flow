package com.spruhs.parkflow.parkinginventory.core.infrastructure.secondary

import com.spruhs.parkflow.parkinginventory.api.GateType
import com.spruhs.parkflow.parkinginventory.core.application.ParkingInventoryRepositoryPort
import com.spruhs.parkflow.parkinginventory.core.domain.ActivationState
import com.spruhs.parkflow.parkinginventory.core.domain.GateName
import com.spruhs.parkflow.parkinginventory.core.domain.GateProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingInventoryProjection
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotName
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotProjection
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Document("gates")
data class GateDocument(
    @Id
    val id: String,
    val name: String,
    val state: String,
    val type: String,
)

@Document("parking_spots")
data class ParkingSpotDocument(
    @Id
    val id: String,
    val name: String,
    val types: List<String>,
    val state: String,
    val price: String?,
)

@Service
class ParkingInventoryRepositoryAdapter(
    private val gateRepository: GateRepository,
    private val parkingSpotRepository: ParkingSpotRepository,
) : ParkingInventoryRepositoryPort {
    override suspend fun getInventory() =
        ParkingInventoryProjection(
            gates = gateRepository.findAll().map { it.toProjection() }.collectList().awaitSingle(),
            parkingSpots = parkingSpotRepository.findAll().map { it.toProjection() }.collectList().awaitSingle(),
        )

    override suspend fun getGate(gateId: String) = gateRepository.findById(gateId)
        .awaitSingleOrNull()
        ?.toProjection()

    override suspend fun getParkingSpot(parkingSpotId: String) = parkingSpotRepository.findById(parkingSpotId)
        .awaitSingleOrNull()
        ?.toProjection()

    override suspend fun save(gateProjection: GateProjection) {
        gateRepository.save(gateProjection.toDocument()).awaitSingle()
    }

    override suspend fun save(parkingSpotProjection: ParkingSpotProjection) {
        parkingSpotRepository.save(parkingSpotProjection.toDocument()).awaitSingle()
    }

    override suspend fun existsGateName(name: GateName) =
        gateRepository.existsByName(name.value).awaitSingle()

    override suspend fun existsParkingSpotName(name: ParkingSpotName) =
        parkingSpotRepository.existsByName(name.value).awaitSingle()

    override suspend fun removeParkingSpot(parkingSpotId: String) {
        parkingSpotRepository.deleteById(parkingSpotId).awaitSingle()
    }

    override suspend fun removeGate(gateId: String) {
        gateRepository.deleteById(gateId).awaitSingle()
    }
}

@Repository
interface GateRepository : ReactiveMongoRepository<GateDocument, String> {
    fun existsByName(name: String): Mono<Boolean>
}

@Repository
interface ParkingSpotRepository : ReactiveMongoRepository<ParkingSpotDocument, String> {
    fun existsByName(name: String): Mono<Boolean>
}

private fun GateDocument.toProjection() =
    GateProjection(
        gateId = id,
        name = name,
        type = GateType.valueOf(type),
        state = ActivationState.valueOf(state)
    )

private fun ParkingSpotDocument.toProjection() =
    ParkingSpotProjection(
        parkingSpotId = id,
        name = name,
        types = types,
        state = ActivationState.valueOf(state),
        price = price
    )

private fun GateProjection.toDocument() =
    GateDocument(
        id = gateId,
        name = name,
        state = state.name,
        type = type.name
    )

private fun ParkingSpotProjection.toDocument() =
    ParkingSpotDocument(
        id = parkingSpotId,
        name = name,
        types = types,
        state = state.name,
        price = price
    )
