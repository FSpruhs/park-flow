package com.spruhs.parkflow.parkingoperation.core.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.spruhs.parkflow.common.es.AggregateRoot
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotCanceledEvent
import com.spruhs.parkflow.customeraccess.api.CustomerParkingSpotRentedEvent
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.GateActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.GateRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.GateType
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesAddedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesRemovedEvent
import com.spruhs.parkflow.parkingoperation.api.ParkingSpotProvidedEvent
import com.spruhs.parkflow.parkingoperation.api.ParkingSpotReprovidedEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleArrivedEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleEnteredParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleLeavedParkingLotEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOffEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnEvent
import com.spruhs.parkflow.parkingoperation.api.VehicleParkedOnWrongParkingSpotEvent
import java.time.LocalDate

class ParkingOperatorAggregate(override val aggregateId: String) : AggregateRoot(aggregateId, TYPE) {
    private val log = getLogger(javaClass)

    val parkingSpots: MutableMap<ParkingSpotId, ParkingSpot> = mutableMapOf()
    val gates: MutableMap<GateId, Gate> = mutableMapOf()
    val vehicles: MutableMap<PlateNumber, Vehicle> = mutableMapOf()

    private var parkingSpotProvider: ParkingSpotProvider = DefaultParkingSpotProvider()

    override fun whenEvent(event: BaseEvent) {
        when (event) {
            is VehicleArrivedEvent -> handleVehicleArrivedEvent(event)
            is VehicleEnteredParkingLotEvent -> handleEnteredEvent(event)
            is VehicleLeavedParkingLotEvent -> handleLeavedEvent(event)
            is VehicleParkedOnEvent -> handleVehicleParkedOnEvent(event)
            is VehicleParkedOffEvent -> handleVehicleParkedOffEvent(event)
            is ParkingSpotProvidedEvent -> handleParkingSpotProvidedEvent(event)
            is VehicleParkedOnWrongParkingSpotEvent -> handleVehicleParkedOnWrongParkingSpot(event)
            is ParkingSpotReprovidedEvent -> handleParkingSpotReprovidedEvent(event)

            is ParkingSpotCreatedEvent -> handleParkingSpotCreatedEvent(event)
            is ParkingSpotRemovedEvent -> handleParkingSpotRemovedEvent(event)
            is ParkingSpotTypesRemovedEvent -> handleParkingSpotTypesRemovedEvent(event)
            is ParkingSpotTypesAddedEvent -> handleParkingSpotTypesAddedEvent(event)
            is ParkingSpotActivatedEvent -> handleParkingSpotActivatedEvent(event)
            is ParkingSpotDeactivatedEvent -> handleParkingSpotDeactivatedEvent(event)

            is GateCreatedEvent -> handleGateCreatedEvent(event)
            is GateActivatedEvent -> handleGateActivatedEvent(event)
            is GateDeactivatedEvent -> handleGateDeactivatedEvent(event)
            is GateRemovedEvent -> handleGateRemovedEvent(event)

            is CustomerParkingSpotRentedEvent -> handleCustomerParkingSpotRentedEvent(event)
            is CustomerParkingSpotCanceledEvent -> handleCustomerParkingSpotCanceledEvent(event)

            else -> UnknownEventTypeException(event)
        }
    }

    private fun fetchParkingSpot(parkingSpotId: String) = parkingSpots[ParkingSpotId(parkingSpotId)]

    private fun fetchGate(gateId: String) = gates[GateId(gateId)]

    private fun handleParkingSpotCreatedEvent(event: ParkingSpotCreatedEvent) {
        parkingSpots[ParkingSpotId(event.aggregateId)] =
            ParkingSpot(ParkingSpotId(event.aggregateId), event.spotTypes.toMutableSet())
    }

    private fun handleParkingSpotRemovedEvent(event: ParkingSpotRemovedEvent) {
        parkingSpots.remove(ParkingSpotId(event.aggregateId))
    }

    private fun handleParkingSpotTypesRemovedEvent(event: ParkingSpotTypesRemovedEvent) {
        fetchParkingSpot(event.aggregateId)?.types?.removeAll(event.types)
    }

    private fun handleParkingSpotTypesAddedEvent(event: ParkingSpotTypesAddedEvent) {
        fetchParkingSpot(event.aggregateId)?.types?.addAll(event.types)
    }

    private fun handleParkingSpotActivatedEvent(event: ParkingSpotActivatedEvent) {
        fetchParkingSpot(event.aggregateId)?.isActive = true
    }

    private fun handleParkingSpotDeactivatedEvent(event: ParkingSpotDeactivatedEvent) {
        fetchParkingSpot(event.aggregateId)?.isActive = false
    }

    private fun handleGateCreatedEvent(event: GateCreatedEvent) {
        when (event.gateType) {
            GateType.ENTRANCE -> gates[GateId(event.aggregateId)] = Gate.Entrance(GateId(event.aggregateId))
            GateType.EXIT -> gates[GateId(event.aggregateId)] = Gate.Exit(GateId(event.aggregateId))
        }
    }

    private fun handleGateActivatedEvent(event: GateActivatedEvent) {
        fetchGate(event.aggregateId)?.isActive = true
    }

    private fun handleGateDeactivatedEvent(event: GateDeactivatedEvent) {
        fetchGate(event.aggregateId)?.isActive = false
    }

    private fun handleGateRemovedEvent(event: GateRemovedEvent) {
        gates.remove(GateId(event.aggregateId))
    }

    private fun handleCustomerParkingSpotRentedEvent(event: CustomerParkingSpotRentedEvent) {
        fetchParkingSpot(event.aggregateId)?.rental = Rental(event.plateNumber, event.rentedAt)
    }

    private fun handleCustomerParkingSpotCanceledEvent(event: CustomerParkingSpotCanceledEvent) =
        fetchParkingSpot(event.aggregateId)
            ?.let { spot ->
                spot.rental?.let { rental ->
                    spot.rental = rental.copy(to = event.endOfRental)
                }
            }

    private fun handleParkingSpotReprovidedEvent(event: ParkingSpotReprovidedEvent) {
        parkingSpots[event.parkingSpotId]?.reservedForVehicle = event.plateNumber
    }

    private fun handleVehicleParkedOnWrongParkingSpot(event: VehicleParkedOnWrongParkingSpotEvent) {
        parkVehicle(event.parkingSpot, event.vehicle)
        if (isParkingSpotRented(event.parkingSpot) == false) {
            parkingSpots[event.parkingSpot.parkingSpotId]?.reservedForVehicle = event.vehicle.plateNumber
        }
        cancelOldReservation(event.vehicle)
    }

    private fun parkVehicle(
        parkingSpot: ParkingSpot,
        vehicle: Vehicle,
    ) {
        parkingSpots[parkingSpot.parkingSpotId]?.parkingVehicle = vehicle.plateNumber
        vehicles[vehicle.plateNumber]?.state = VehicleAction.OnParkingSpot(parkingSpot.parkingSpotId)
    }

    private fun parkVehicle(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) {
        parkingSpots[parkingSpotId]?.parkingVehicle = plateNumber
        vehicles[plateNumber]?.state = VehicleAction.OnParkingSpot(parkingSpotId)
    }

    private fun cancelOldReservation(vehicle: Vehicle) {
        parkingSpots.values
            .forEach { if (it.reservedForVehicle == vehicle.plateNumber) it.reservedForVehicle = null }
    }

    private fun isParkingSpotRented(parkingSpot: ParkingSpot) = parkingSpots[parkingSpot.parkingSpotId]?.isRented()

    private fun handleVehicleParkedOnEvent(event: VehicleParkedOnEvent) {
        parkVehicle(event.parkingSpotId, event.plateNumber)
    }

    private fun handleVehicleArrivedEvent(event: VehicleArrivedEvent) {
        vehicles[event.vehicle.plateNumber] = event.vehicle
    }

    private fun handleParkingSpotProvidedEvent(event: ParkingSpotProvidedEvent) {
        parkingSpots[event.parkingSpotId]?.reservedForVehicle = event.plateNumber
    }

    private fun handleEnteredEvent(event: VehicleEnteredParkingLotEvent) {
        vehicles.remove(event.plateNumber)
    }

    private fun handleLeavedEvent(event: VehicleLeavedParkingLotEvent) {
        vehicles[event.plateNumber]?.state = VehicleAction.DrivingAround
    }

    private fun handleVehicleParkedOffEvent(event: VehicleParkedOffEvent) {
        vehicles[event.plateNumber]?.state = VehicleAction.DrivingAround
        if (parkingSpots[event.parkingSpotId]?.isRented() == false) {
            parkingSpots[event.parkingSpotId]?.reservedForVehicle = null
        }
        unParkVehicle(event.parkingSpotId, event.plateNumber)
    }

    private fun unParkVehicle(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) {
        parkingSpots[parkingSpotId]?.parkingVehicle = null
        vehicles[plateNumber]?.state = VehicleAction.DrivingAround
    }

    fun onVehicleArrival(
        gateId: GateId,
        plateNumber: PlateNumber,
        hasDisabilityCard: Boolean,
    ): GateResponse {
        val gate = gates[gateId] ?: return GateResponse.Error.NotFoundError
        val arrivedVehicle = Vehicle(plateNumber, hasDisabilityCard, VehicleAction.OnGate(gate))

        return determineArriveAction(gate, arrivedVehicle)
            .also { apply(VehicleArrivedEvent(aggregateId, gateId, arrivedVehicle)) }
    }

    private fun determineArriveAction(
        gate: Gate,
        vehicle: Vehicle,
    ) = when (gate) {
        is Gate.Entrance -> onEntranceArrival(vehicle)
        is Gate.Exit -> onExitArrival()
    }

    fun onVehicleDroveThrough(
        gateId: GateId,
        plateNumber: PlateNumber,
    ) {
        when (gates[gateId] ?: return) {
            is Gate.Entrance ->
                apply(
                    VehicleEnteredParkingLotEvent(
                        aggregateId,
                        gateId,
                        plateNumber,
                        vehicles[plateNumber]?.hasDisabilityCard ?: false,
                    ),
                )

            is Gate.Exit -> apply(VehicleLeavedParkingLotEvent(aggregateId, gateId, plateNumber))
        }
    }

    fun onVehicleParkedOn(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) {
        val parkingSpot = parkingSpots[parkingSpotId] ?: return
        if (parkingSpot.parkingVehicle != null) {
            log.error("CRASH on $parkingSpotId is already vehicle parked!")
        }

        if (parkingSpot.reservedForVehicle == plateNumber) {
            parkCorrect(parkingSpotId, plateNumber)
        } else {
            parkIncorrect(plateNumber, parkingSpot)
        }
    }

    private fun parkCorrect(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) {
        apply(VehicleParkedOnEvent(aggregateId, parkingSpotId, plateNumber))
    }

    private fun parkIncorrect(
        plateNumber: PlateNumber,
        parkingSpot: ParkingSpot,
    ) {
        val vehicle = vehicles[plateNumber] ?: return
        apply(
            VehicleParkedOnWrongParkingSpotEvent(
                aggregateId,
                vehicle,
                parkingSpot.reservedForVehicle,
                parkingSpot,
            ),
        )
        if (parkingSpot.reservedForVehicle != null) {
            reprovideParkingSpot(parkingSpot)
        }
    }

    private fun reprovideParkingSpot(parkingSpot: ParkingSpot) {
        val plateNumber = parkingSpot.reservedForVehicle ?: return

        val reservedFor = vehicles[plateNumber] ?: return
        val newSpot = findParkingSpotFor(reservedFor) ?: return

        apply(ParkingSpotReprovidedEvent(aggregateId, newSpot.parkingSpotId, plateNumber))
    }

    private fun onExitArrival() = GateResponse.Action.LetVehicleOut

    private fun onEntranceArrival(vehicle: Vehicle): GateResponse {
        val spot =
            findParkingSpotFor(vehicle)
                ?: return GateResponse.Error.NoParkingSpotAvailableError

        apply(ParkingSpotProvidedEvent(aggregateId, vehicle.plateNumber, spot.parkingSpotId))
        return GateResponse.Action.ProvideParkingSpot(spot.parkingSpotId)
    }

    private fun findParkingSpotFor(vehicle: Vehicle) = parkingSpotProvider.provide(parkingSpots, vehicle)

    fun onVehicleParkedOff(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) = apply(VehicleParkedOffEvent(aggregateId, parkingSpotId, plateNumber))

    companion object {
        const val TYPE = "ParkingOperator"
    }
}

fun interface ParkingSpotProvider {
    fun provide(
        parkingSpots: Map<ParkingSpotId, ParkingSpot>,
        vehicle: Vehicle,
    ): ParkingSpot?
}

class DefaultParkingSpotProvider : ParkingSpotProvider {
    override fun provide(
        parkingSpots: Map<ParkingSpotId, ParkingSpot>,
        vehicle: Vehicle,
    ): ParkingSpot? {
        findRentedParkingSpot(parkingSpots, vehicle)?.let { return it }

        val freeSpots = findFreeSpots(parkingSpots)
        if (freeSpots.isEmpty()) return null

        buildPrioritizedTypesSets(vehicle).forEach { requiredTypes ->
            freeSpots.firstOrNull { it.types.containsAll(requiredTypes) }?.let { return it }
        }

        return freeSpots.firstOrNull()
    }

    private fun buildPrioritizedTypesSets(vehicle: Vehicle): List<Set<ParkingSpotType>> {
        val prioritizedTypes = mutableListOf<Set<ParkingSpotType>>()

        if (vehicle.plateNumber.isElectrical() && vehicle.hasDisabilityCard) {
            prioritizedTypes.add(setOf(ParkingSpotType.Electric, ParkingSpotType.Disabled))
        }
        if (vehicle.plateNumber.isElectrical()) {
            prioritizedTypes.add(setOf(ParkingSpotType.Electric))
        }
        if (vehicle.hasDisabilityCard) {
            prioritizedTypes.add(setOf(ParkingSpotType.Disabled))
        }
        return prioritizedTypes
    }

    private fun findRentedParkingSpot(
        parkingSpots: Map<ParkingSpotId, ParkingSpot>,
        vehicle: Vehicle,
    ) = parkingSpots.values.firstOrNull { it.isRented() && it.reservedForVehicle == vehicle.plateNumber }

    private fun findFreeSpots(parkingSpots: Map<ParkingSpotId, ParkingSpot>) =
        parkingSpots.values
            .filter { it.parkingVehicle == null && it.reservedForVehicle == null && !it.isRented() }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(Gate.Entrance::class, name = "Entrance"),
    JsonSubTypes.Type(Gate.Exit::class, name = "Exit"),
)
sealed class Gate(open val gateId: GateId, open var isActive: Boolean = true) {
    data class Exit(override val gateId: GateId, override var isActive: Boolean = true) : Gate(gateId, isActive)

    data class Entrance(override val gateId: GateId, override var isActive: Boolean = true) : Gate(gateId, isActive)
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ParkingSpot(
    val parkingSpotId: ParkingSpotId,
    val types: MutableSet<ParkingSpotType> = mutableSetOf(),
    var parkingVehicle: PlateNumber? = null,
    var reservedForVehicle: PlateNumber? = null,
    var isActive: Boolean = true,
    var rental: Rental? = null,
) {
    fun isRented(): Boolean {
        if (rental == null) {
            return false
        }

        val today = LocalDate.now()

        if (today.isBefore(rental?.from)) return false

        if (rental?.to != null && today.isAfter(rental?.to)) return false

        if (rental?.to == null && today.isAfter(rental?.from)) return false

        return true
    }
}

data class Rental(
    val plateNumber: PlateNumber,
    val from: LocalDate,
    val to: LocalDate? = null,
)

data class Vehicle(
    val plateNumber: PlateNumber,
    val hasDisabilityCard: Boolean = false,
    var state: VehicleAction,
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = VehicleAction.DrivingAround::class, name = "DrivingAround"),
    JsonSubTypes.Type(value = VehicleAction.OnGate::class, name = "OnGate"),
    JsonSubTypes.Type(value = VehicleAction.OnParkingSpot::class, name = "OnParkingSpot"),
)
sealed class VehicleAction {
    object DrivingAround : VehicleAction()

    data class OnGate(val gate: Gate) : VehicleAction()

    data class OnParkingSpot(val parkingSpotId: ParkingSpotId) : VehicleAction()
}

sealed class GateResponse {
    sealed class Error : GateResponse() {
        object PlateNumberNotRegisteredError : Error()

        object NoParkingSpotAvailableError : Error()

        object NotFoundError : Error()
    }

    sealed class Action : GateResponse() {
        data class ProvideParkingSpot(val parkingSpotId: ParkingSpotId) : Action()

        object LetVehicleOut : Action()
    }
}
