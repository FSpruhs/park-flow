package com.spruhs.parkflow.customeraccess.core.application

import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.customeraccess.core.domain.CustomerProjection
import com.spruhs.parkflow.customeraccess.core.domain.VehicleProjection
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class CustomerListServiceTest {
    @MockK
    lateinit var repository: CustomerListRepositoryPort

    @InjectMockKs
    lateinit var service: CustomerListService

    @Test
    fun `isParkingSpotRented should return true when is rented`(): Unit =
        runBlocking {
            val parkingSpotId = ParkingSpotId("123")
            val plateNumber = PlateNumber("K-A1")

            val customer =
                CustomerProjection(
                    "456",
                    listOf(
                        VehicleProjection(
                            plateNumber = plateNumber.value,
                            rentedParkingSpotId = parkingSpotId.value,
                            rentedFrom = LocalDate.now().toString(),
                            rentedTo = null,
                        ),
                    ),
                    "PayPal",
                )

            coEvery { repository.findByPlateNumber(plateNumber.value) } returns customer

            val result = service.isParkingSpotRented(parkingSpotId, plateNumber)

            assertThat(result).isTrue()
        }
}
