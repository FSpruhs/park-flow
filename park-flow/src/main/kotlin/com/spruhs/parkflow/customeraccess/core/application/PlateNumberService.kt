package com.spruhs.parkflow.customeraccess.core.application

import com.spruhs.parkflow.customeraccess.api.PlateNumber
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import kotlin.collections.component1
import kotlin.collections.component2

@Service
class PlateNumberService(
    private val customerListRepositoryPort: CustomerListRepositoryPort,
) {
    private val reservedPlateNumbers: MutableMap<PlateNumber, Instant> = mutableMapOf()

    @Scheduled(fixedRate = 60 * 1000)
    private fun cleanupExpiredReservations() {
        reservedPlateNumbers.entries.removeIf { (_, reservedAt) -> isReservationTimeOver(reservedAt, Instant.now()) }
    }

    private fun isReservationTimeOver(
        reservedAt: Instant,
        now: Instant,
    ) = Duration.between(reservedAt, now).toMinutes() > RESERVATION_TIME_IN_MINUTES

    suspend fun existsPlateNumber(plateNumber: PlateNumber): Boolean =
        reservedPlateNumbers.containsKey(plateNumber) ||
            customerListRepositoryPort.findByPlateNumber(plateNumber.value) != null

    suspend fun reservePlateNumber(plateNumber: PlateNumber) {
        require(!existsPlateNumber(plateNumber)) { "Plate number ${plateNumber.value} already exists" }
        reservedPlateNumbers[plateNumber] = Instant.now()
    }

    suspend fun handlePlateNumberAdded(plateNumber: PlateNumber) {
        reservedPlateNumbers.remove(plateNumber)
    }

    companion object {
        private const val RESERVATION_TIME_IN_MINUTES = 5
    }
}
