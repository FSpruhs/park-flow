package com.spruhs.parkflow.billing.core.application

import com.spruhs.parkflow.billing.core.domain.HistoryItem
import com.spruhs.parkflow.billing.core.domain.HistoryType
import com.spruhs.parkflow.billing.core.domain.VehicleHistoryReflection
import com.spruhs.parkflow.customeraccess.api.CustomerApi
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.ParkingInventoryApi
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.Instant

@ExtendWith(MockKExtension::class)
class InvoiceServiceTest {
    @MockK
    lateinit var paymentPort: PaymentPort

    @MockK
    lateinit var customerApi: CustomerApi

    @MockK
    lateinit var parkingInventoryApi: ParkingInventoryApi

    @MockK
    lateinit var invoiceRepositoryPort: InvoiceRepositoryPort

    @InjectMockKs
    lateinit var service: InvoiceService

    @Test
    fun `invoice should invoice correct when parked wrong`(): Unit =
        runBlocking {
            val history =
                VehicleHistoryReflection(
                    plateNumber = PlateNumber("K-A1"),
                    customerId = "123",
                    history =
                        listOf(
                            HistoryItem(Instant.now(), HistoryType.CREATED),
                            HistoryItem(Instant.now().plusMillis(100), HistoryType.ENTER),
                            HistoryItem(
                                Instant.now().plusMillis(200),
                                HistoryType.PARKED_ON_WRONG,
                                parkingSpotId = "456",
                            ),
                            HistoryItem(Instant.now().plusMillis(500_000), HistoryType.PARKED_OFF),
                        ),
                )

            coEvery { paymentPort.charge(any()) } returns Unit
            coEvery { invoiceRepositoryPort.save(any()) } returns Unit
            coEvery { customerApi.isParkingSpotRented(ParkingSpotId("456"), PlateNumber("K-A1")) } returns false
            coEvery { parkingInventoryApi.getParkingSpotTypes(ParkingSpotId("456")) } returns emptyList()

            val result = service.invoice(history, Instant.now().plusMillis(600_000))

            assertThat(result.totalAmount).isEqualTo(BigDecimal("20"))
        }
}
