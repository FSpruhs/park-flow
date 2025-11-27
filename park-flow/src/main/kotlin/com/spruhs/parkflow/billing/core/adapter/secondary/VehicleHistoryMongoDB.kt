package com.spruhs.parkflow.billing.core.adapter.secondary

import com.spruhs.parkflow.billing.core.application.VehicleHistoryRepositoryPort
import com.spruhs.parkflow.billing.core.domain.HistoryItem
import com.spruhs.parkflow.billing.core.domain.VehicleHistoryReflection
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Document("vehicle_history")
data class VehicleHistoryDocument(
    @Id
    val plateNumber: String,
    val customerId: String,
    val history: List<HistoryItem>,
)

@Component
class VehicleHistoryMongoDBAdapter(private val repository: VehicleHistoryRepository) : VehicleHistoryRepositoryPort {
    override suspend fun save(vehicleHistory: VehicleHistoryReflection) {
        repository.save(vehicleHistory.toDocument()).awaitSingle()
    }

    override suspend fun findByPlateNumber(plateNumber: PlateNumber): VehicleHistoryReflection? {
        return repository.findById(plateNumber.value).awaitSingleOrNull()?.toReflection()
            ?: throw IllegalArgumentException("Customer with id ${plateNumber.value} not found")
    }
}

@Repository
interface VehicleHistoryRepository : ReactiveMongoRepository<VehicleHistoryDocument, String>

private fun VehicleHistoryReflection.toDocument() =
    VehicleHistoryDocument(
        plateNumber = this.plateNumber.value,
        customerId = this.customerId,
        history = this.history,
    )

private fun VehicleHistoryDocument.toReflection() =
    VehicleHistoryReflection(
        plateNumber = PlateNumber(this.plateNumber),
        customerId = this.customerId,
        history = this.history,
    )
