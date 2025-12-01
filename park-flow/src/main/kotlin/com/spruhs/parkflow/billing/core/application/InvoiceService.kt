package com.spruhs.parkflow.billing.core.application

import com.spruhs.parkflow.billing.core.domain.Invoice
import com.spruhs.parkflow.billing.core.domain.VehicleHistoryReflection
import com.spruhs.parkflow.customeraccess.api.CustomerApi
import com.spruhs.parkflow.customeraccess.api.CustomerId
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.ParkingInventoryApi
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class InvoiceService(
    private val paymentPort: PaymentPort,
    private val customerApi: CustomerApi,
    private val parkingInventoryApi: ParkingInventoryApi,
    private val repository: InvoiceRepositoryPort,
) {
    suspend fun getAll() = repository.getAll()

    suspend fun invoice(
        history: VehicleHistoryReflection,
        leaveTime: Instant,
    ) = Invoice.create(
        customerId = CustomerId(history.customerId),
        plateNumber = history.plateNumber,
        history = history.history,
        leaveTime = leaveTime,
        isParkingSpotRented = ::isParkingSpotRented,
        fetchTypes = ::fetchTypes,
    ).also {
        paymentPort.charge(it)
        repository.save(it)
    }

    private suspend fun isParkingSpotRented(
        parkingSpotId: String,
        plateNumber: PlateNumber,
    ) = customerApi.isParkingSpotRented(ParkingSpotId(parkingSpotId), plateNumber)

    private suspend fun fetchTypes(parkingSpotId: String) =
        parkingInventoryApi.getParkingSpotTypes(ParkingSpotId(parkingSpotId))
}

fun interface PaymentPort {
    suspend fun charge(invoice: Invoice)
}

interface InvoiceRepositoryPort {
    suspend fun save(invoice: Invoice)

    suspend fun getAll(): List<Invoice>
}
