package com.spruhs.parkflow.billing.core.infrastructure.secondary

import com.spruhs.parkflow.billing.core.application.InvoiceRepositoryPort
import com.spruhs.parkflow.billing.core.domain.Invoice
import com.spruhs.parkflow.billing.core.domain.InvoiceId
import com.spruhs.parkflow.billing.core.domain.InvoiceItem
import com.spruhs.parkflow.customeraccess.api.CustomerId
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

@Document(collection = "invoices")
data class InvoiceDocument(
    @Id
    val id: String,
    val customerId: String,
    val plateNumber: String,
    val totalAmount: String,
    val items: List<InvoiceItem>,
)

@Component
class InvoiceRepositoryAdapter(private val repository: InvoiceRepository) : InvoiceRepositoryPort {
    override suspend fun save(invoice: Invoice) {
        repository.save(invoice.toDocument()).awaitSingle()
    }

    override suspend fun getAll(): List<Invoice> =
        repository.findAll()
            .map { it.toInvoice() }
            .collectList()
            .awaitSingle()

}

@Repository
interface InvoiceRepository : ReactiveMongoRepository<InvoiceDocument, String>


private fun Invoice.toDocument() = InvoiceDocument(
    id = invoiceId.value,
    customerId = customerId.value,
    plateNumber = plateNumber.value,
    totalAmount = totalAmount.toString(),
    items = items
)

private fun InvoiceDocument.toInvoice() = Invoice(
    invoiceId = InvoiceId(id),
    customerId = CustomerId(customerId),
    plateNumber = PlateNumber(plateNumber),
    totalAmount = totalAmount.toBigDecimal(),
    items = items.toMutableList()
)
