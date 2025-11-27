package com.spruhs.parkflowsimulator.scenario

import com.spruhs.parkflowsimulator.webclient.ParkFlowWebClientService
import com.spruhs.parkflowsimulator.webclient.VehicleResponse
import kotlinx.coroutines.coroutineScope
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@ConditionalOnProperty(
    name = ["simulation.scenario"],
    havingValue = "simple-customer-access",
    matchIfMissing = false
)
class SimpleCustomerAccessScenario(webClient: ParkFlowWebClientService) :
    Scenario(webClient) {

    private val parkingSpots = listOf(
        ParkingSpotInfo("A-1"),
        ParkingSpotInfo("A-2", listOf("DISABLED")),
        ParkingSpotInfo("B-1", listOf("RENTABLE", "ELECTRIC"), "10.05"),
        ParkingSpotInfo("B-2", listOf("RENTABLE"), "11.05"),
        ParkingSpotInfo("C-1", listOf("RENTABLE"), "12.05"),
        ParkingSpotInfo("C-2", listOf("RENTABLE"), "13.05"),
        ParkingSpotInfo("C-3", listOf("RENTABLE", "ELECTRIC"), "14.05"),
        ParkingSpotInfo("C-4", listOf("RENTABLE"), "15.05"),
        ParkingSpotInfo("C-5", listOf("RENTABLE"), "16.05"),
    )

    private val customers = listOf(
        CustomerInfo("K-A1"),
        CustomerInfo("K-A2E"),
        CustomerInfo("K-A3H")
    )

    private val newPlates = listOf(
        "K-B1E",
        "K-B2",
        "K-B3"
    )

    private val actionList = listOf(
        *parkingSpots.map { ScenarioAction.CreateParkingSpot(it) }.toTypedArray(),
        *customers.map { ScenarioAction.CreateCustomer(it) }.toTypedArray(),

        ScenarioAction.Custom { changePaymentMethod(customers[0], "Mastercard") },
        ScenarioAction.Custom { addVehicle(customers[1], newPlates[0]) },
        ScenarioAction.Custom { addVehicle(customers[2], newPlates[1]) },
        ScenarioAction.Custom { addVehicle(customers[2], newPlates[2]) },

        ScenarioAction.ExpectError(400) { addVehicle(customers[0], newPlates[0]) },
        ScenarioAction.ExpectError(404) { webClient.addVehicle("does not exists", "X-X1") },

        ScenarioAction.Custom { removeVehicle(customers[1], customers[1].plateNumber) },
        ScenarioAction.Custom { rentParkingSpot(customers[2], parkingSpots[3], newPlates[1]) },

        ScenarioAction.ExpectError(400) {
            rentParkingSpot(
                customers[0],
                parkingSpots[2],
                customers[0].plateNumber
            )
        },
        ScenarioAction.ExpectError(400) { rentParkingSpot(customers[2], parkingSpots[4], newPlates[1]) },

        ScenarioAction.Custom { rentParkingSpot(customers[1], parkingSpots[2], newPlates[0]) },
        ScenarioAction.Custom { cancelParkingSpot(customers[2], parkingSpots[3]) },

        ScenarioAction.ExpectError(400) { rentParkingSpot(customers[2], parkingSpots[0], newPlates[2]) },

        ScenarioAction.Custom { deactivateParkingSpot(parkingSpots[5]) },
        ScenarioAction.Custom { deactivateParkingSpot(parkingSpots[8]) },
        ScenarioAction.Custom { removeParkingSpot(parkingSpots[7]) },
        ScenarioAction.Custom { removeParkingSpotType(parkingSpots[6], listOf("ELECTRIC")) },
        ScenarioAction.Custom { addParkingSpotType(parkingSpots[8], listOf("ELECTRIC")) },
        ScenarioAction.Custom { activateParkingSpot(parkingSpots[5]) },

        ScenarioAction.ExpectError(400) {
            rentParkingSpot(
                customers[0],
                parkingSpots[8],
                customers[0].plateNumber
            )
        },
        ScenarioAction.ExpectError(400) {
            rentParkingSpot(
                customers[0],
                parkingSpots[7],
                customers[0].plateNumber
            )
        }
    )

    override suspend fun start(): Unit = coroutineScope {
        runActions(actionList)

        log.info("------ Scenario ended ------")
        log.info("------ Start validating scenario ------")

        validateScenario()

        log.info("------ Scenario validation correct ------")
    }

    private suspend fun validateScenario() {
        val parkingCatalog = webClient.getParkingSpotCatalog()
        val customerList = webClient.getCustomerList()

        if (parkingCatalog.parkingSpots.count() != 6) error("parking spot count should be 6 and is actual ${parkingCatalog.parkingSpots.count()}")
        if (customerList.customers.count() != 3) error("customer count should be 3 and is actual ${customerList.customers.count()}")

        val parkingSpotCatalogValidators = listOf(
            ParkingSpotCatalogValidator(parkingSpots[2].id(), "10.05", true, true, null),
            ParkingSpotCatalogValidator(
                parkingSpots[3].id(),
                "11.05",
                false,
                true,
                LocalDate.now().plusMonths(1).plusDays(1).toString()
            ),
            ParkingSpotCatalogValidator(parkingSpots[4].id(), "12.05", false, true, LocalDate.now().toString()),
            ParkingSpotCatalogValidator(parkingSpots[5].id(), "13.05", false, true, LocalDate.now().toString()),
            ParkingSpotCatalogValidator(parkingSpots[6].id(), "14.05", false, true, LocalDate.now().toString()),
            ParkingSpotCatalogValidator(parkingSpots[8].id(), "16.05", true, false, LocalDate.now().toString()),
        )

        val expectedVehicles = listOf(
            VehicleResponse(customers[0].plateNumber, null, null, null),
            VehicleResponse(customers[2].plateNumber, null, null, null),
            VehicleResponse(newPlates[0], parkingSpots[2].id(), LocalDate.now().toString(), null),
            VehicleResponse(
                newPlates[1],
                parkingSpots[3].id(),
                LocalDate.now().toString(),
                LocalDate.now().plusMonths(1).toString()
            ),
            VehicleResponse(newPlates[2], null, null, null),
        )

        val customerListValidators = listOf(
            CustomerValidator(customers[0].id(), "Mastercard", listOf(expectedVehicles[0])),
            CustomerValidator(customers[1].id(), customers[0].paymentMethodId, listOf(expectedVehicles[2])),
            CustomerValidator(
                customers[2].id(),
                customers[0].paymentMethodId,
                listOf(expectedVehicles[1], expectedVehicles[3], expectedVehicles[4])
            )
        )

        validateAll(parkingCatalog.parkingSpots, parkingSpotCatalogValidators) { it.parkingSpotId }
        validateAll(customerList.customers, customerListValidators) { it.id }
    }
}
