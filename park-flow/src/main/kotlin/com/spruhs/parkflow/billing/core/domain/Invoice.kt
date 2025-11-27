package com.spruhs.parkflow.billing.core.domain

import java.math.BigDecimal
import java.time.Duration

data class Invoice(
    val items: List<InvoiceItem>,
    val totalAmount: BigDecimal = BigDecimal.ZERO,
)

data class InvoiceItem(val amount: BigDecimal, val feePosition: FeePosition)

fun Invoice.addParkingPerHour(item: FeePosition.ParkingPerHour) {
    this.add(item)
}

fun Invoice.addExtraCharges(items: List<FeePosition>) {
    items.forEach { this.add(it) }
}

private fun Invoice.add(position: FeePosition): Invoice {
    val item = position.calculateInfoItem()
    return Invoice(
        items = this.items + item,
        totalAmount = this.totalAmount + item.amount
    )
}

private fun FeePosition.calculateInfoItem(): InvoiceItem {
    return when (this) {
        FeePosition.ParkingOnWrongSpot -> InvoiceItem(this.price ,this)
        is FeePosition.ParkingPerHour -> InvoiceItem(this.price * this.duration.toHours().toBigDecimal() ,this)
        FeePosition.UnauthorizedParkingOnDisabledSpot -> InvoiceItem(this.price ,this)
        FeePosition.UnauthorizedParkingOnElectricSpot -> InvoiceItem(this.price ,this)
        FeePosition.UnauthorizedParkingOnRentedSpot -> InvoiceItem(this.price ,this)
    }
}

sealed class FeePosition(open val price: BigDecimal) {

    object ParkingOnWrongSpot : FeePosition(BigDecimal("10"))
    object UnauthorizedParkingOnDisabledSpot : FeePosition(BigDecimal("100"))
    object UnauthorizedParkingOnRentedSpot : FeePosition(BigDecimal("80"))
    object UnauthorizedParkingOnElectricSpot : FeePosition(BigDecimal("50"))
    data class ParkingPerHour(val duration: Duration) : FeePosition(BigDecimal("10"))
}
