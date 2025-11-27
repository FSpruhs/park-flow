package com.spruhs.parkflow.billing.core.adapter.secondary

import com.spruhs.parkflow.billing.core.application.PaymentPort
import com.spruhs.parkflow.common.helper.getLogger
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PaymentDummyAdapter : PaymentPort {
    private val log = getLogger(javaClass)

    override suspend fun charge(invoice: List<Pair<String, BigDecimal>>) {
        log.info("Charging invoice with items: $invoice")
    }
}
