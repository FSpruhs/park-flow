package com.spruhs.parkflow.billing.core.adapter.primary

import com.spruhs.parkflow.billing.core.application.InvoiceQueryPort
import com.spruhs.parkflow.billing.core.domain.Invoice
import com.spruhs.parkflow.billing.core.domain.InvoiceItem
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/billing/invoice")
class InvoiceRestAdapter(private val queryPort: InvoiceQueryPort) {
    @GetMapping
    suspend fun getInvoices() = queryPort.getInvoices().map { it.toMessage() }
}

data class InvoiceMessage(
    val invoiceId: String,
    val customerId: String,
    val plateNumber: String,
    val totalAmount: String,
    val items: List<InvoiceItemMessage>
)

data class InvoiceItemMessage(
    val feePosition: String,
    val amount: String,
)

private fun Invoice.toMessage() = InvoiceMessage(
    invoiceId = invoiceId.value,
    customerId = customerId.value,
    plateNumber = plateNumber.value,
    totalAmount = totalAmount.toString(),
    items = items.map { it.toMessage() }
)

private fun InvoiceItem.toMessage() = InvoiceItemMessage(
    feePosition = feePosition.name(),
    amount = amount.toString(),
)
