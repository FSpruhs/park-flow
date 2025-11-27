package com.spruhs.parkflow.parkingoperation.core.adapter.secondary

import com.spruhs.parkflow.customeraccess.api.CustomerApi
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkingoperation.core.application.CustomerOperationApiPort
import org.springframework.stereotype.Component

@Component
class CustomerOperationApiAdapter(private val customerApi: CustomerApi) : CustomerOperationApiPort {
    override suspend fun isPlateNumberRegistered(plateNumber: PlateNumber) =
        customerApi.isPlateNumberRegistered(plateNumber)
}
