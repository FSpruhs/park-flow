package com.spruhs.parkflow.customeraccess.core.infrastructure.secondary

import com.spruhs.parkflow.customeraccess.core.application.CustomerListRepositoryPort
import com.spruhs.parkflow.customeraccess.core.domain.CustomerListProjection
import com.spruhs.parkflow.customeraccess.core.domain.CustomerProjection
import com.spruhs.parkflow.customeraccess.core.domain.VehicleProjection
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Document("customers")
@CompoundIndex(def = "{ 'vehicles.plateNumber': 1 }")
data class CustomerDocument(
    @Id
    val id: String,
    val paymentMethodId: String,
    val vehicles: List<VehicleProjection>,
)

@Component
class CustomerListRepositoryAdapter(private val repository: CustomerListRepository) : CustomerListRepositoryPort {
    override suspend fun save(customer: CustomerProjection) {
        repository.save(customer.toDocument()).awaitSingle()
    }

    override suspend fun findByPlateNumber(plateNumber: String) =
        repository.findByPlateNumber(plateNumber)
            .awaitFirstOrNull()
            ?.toProjection()

    override suspend fun findById(id: String) =
        repository.findById(id)
            .awaitSingleOrNull()
            ?.toProjection()

    override suspend fun findAll() =
        repository.findAll()
            .map { it.toProjection() }
            .collectList()
            .awaitSingle()
            .let { CustomerListProjection(it) }
}

@Repository
interface CustomerListRepository : ReactiveMongoRepository<CustomerDocument, String> {
    @Query("{ 'vehicles.plateNumber': ?0 }")
    fun findByPlateNumber(plateNumber: String): Flux<CustomerDocument>
}

private fun CustomerProjection.toDocument() =
    CustomerDocument(
        id = this.customerId,
        paymentMethodId = this.paymentMethodId,
        vehicles = this.vehicles,
    )

private fun CustomerDocument.toProjection() =
    CustomerProjection(
        customerId = this.id,
        paymentMethodId = this.paymentMethodId,
        vehicles = this.vehicles,
    )
