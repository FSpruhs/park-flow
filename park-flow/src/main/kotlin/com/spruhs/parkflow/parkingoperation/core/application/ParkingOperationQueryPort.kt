package com.spruhs.parkflow.parkingoperation.core.application

import org.springframework.stereotype.Service

@Service
class ParkingOperationQueryPort(private val service: ParkingOperatorService) {

    suspend fun getLiveMap() = service.getLiveMap()
}
