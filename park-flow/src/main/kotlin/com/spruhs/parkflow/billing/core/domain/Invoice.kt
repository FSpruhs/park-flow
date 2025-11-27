package com.spruhs.parkflow.billing.core.domain

import com.spruhs.parkflow.customeraccess.api.CustomerId
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import java.math.BigDecimal
import java.time.Duration

data class Invoice(
    val invoiceId: InvoiceId,
    val customerId: CustomerId,
    val plateNumber: PlateNumber,
    val items: MutableList<InvoiceItem> = mutableListOf(),
    var totalAmount: BigDecimal = BigDecimal.ZERO,
)

data class InvoiceItem(val amount: BigDecimal, val feePosition: FeePosition)

fun Invoice.add(position: FeePosition) {
    val item = position.calculateInfoItem()
    this.items.add(item)
    this.totalAmount += item.amount
}

fun Invoice.addAll(position: List<FeePosition>) {
    val items = position.map { it.calculateInfoItem() }
    this.items.addAll(items)
    this.totalAmount += items.sumOf { it.amount }
}

private fun FeePosition.calculateInfoItem(): InvoiceItem {
    return when (this) {
        FeePosition.ParkingOnWrongSpot -> InvoiceItem(this.price, this)
        is FeePosition.ParkingPerHour -> InvoiceItem(this.price * maxOf(1, duration.toHours()).toBigDecimal(), this)
        FeePosition.UnauthorizedParkingOnDisabledSpot -> InvoiceItem(this.price, this)
        FeePosition.UnauthorizedParkingOnElectricSpot -> InvoiceItem(this.price, this)
        FeePosition.UnauthorizedParkingOnRentedSpot -> InvoiceItem(this.price, this)
    }
}

sealed class FeePosition(open val price: BigDecimal) {
    object ParkingOnWrongSpot : FeePosition(BigDecimal("10"))

    object UnauthorizedParkingOnDisabledSpot : FeePosition(BigDecimal("100"))

    object UnauthorizedParkingOnRentedSpot : FeePosition(BigDecimal("80"))

    object UnauthorizedParkingOnElectricSpot : FeePosition(BigDecimal("50"))

    data class ParkingPerHour(val duration: Duration) : FeePosition(BigDecimal("10"))

    fun name() = when (this) {
        is ParkingPerHour ->  "Parking per hour"
        ParkingOnWrongSpot -> "Parking on wrong spot"
        UnauthorizedParkingOnDisabledSpot -> "Unauthorized parking on disabled spot"
        UnauthorizedParkingOnElectricSpot -> "Unauthorized parking on electric spot"
        UnauthorizedParkingOnRentedSpot -> "Unauthorized parking on rented spot"
    }
}

@JvmInline
value class InvoiceId(val value: String) {
    init {
        require(value.isNotBlank()) { "Identifier cannot be blank" }
    }
}
