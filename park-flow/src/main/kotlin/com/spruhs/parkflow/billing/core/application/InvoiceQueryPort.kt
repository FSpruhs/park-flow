package com.spruhs.parkflow.billing.core.application

import org.springframework.stereotype.Component

@Component
class InvoiceQueryPort(private val service: InvoiceService) {
    suspend fun getInvoices() = service.getAll()
}
