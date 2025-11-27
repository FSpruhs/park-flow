package com.spruhs.parkflow.parkingoperation.core.domain

import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.GateCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.GateType
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.core.domain.ActivationState
import com.spruhs.parkflow.parkinginventory.core.domain.GateName
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ParkingOperatorAggregateTest {
    @Test
    fun `onVehicleArrival should return error when no parking spot available`() {
        val operator = ParkingOperatorAggregate("1")

        operator.apply(ParkingSpotCreatedEvent("1", ParkingSpotName("A-1"), emptySet(), ActivationState.ACTIVE))
        operator.apply(GateCreatedEvent("2", GateType.ENTRANCE, GateName("G-1")))

        val response1 = operator.onVehicleArrival(GateId("2"), PlateNumber("K-A1"), false)
        val response2 = operator.onVehicleArrival(GateId("2"), PlateNumber("K-A2"), false)

        assertThat(response1).isEqualTo(GateResponse.Action.ProvideParkingSpot(ParkingSpotId("1")))
        assertThat(response2).isEqualTo(GateResponse.Error.NoParkingSpotAvailableError)
    }
}
