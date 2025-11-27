package com.spruhs.parkflow.parkingoperation.core.application

import com.spruhs.parkflow.common.configs.EventExecutionStrategy
import com.spruhs.parkflow.common.es.AggregateNotFoundException
import com.spruhs.parkflow.common.es.AggregateStore
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.asImported
import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.customeraccess.api.PlateNumber
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkingoperation.api.ParkingSpotReprovidedEvent
import com.spruhs.parkflow.parkingoperation.core.domain.GateResponse
import com.spruhs.parkflow.parkingoperation.core.domain.ParkingOperatorAggregate
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class ParkingOperatorService(
    private val store: AggregateStore,
    private val gateController: GateControllerPort,
    private val customerPort: CustomerOperationApiPort,
    eventExecutionStrategy: EventExecutionStrategy,
) {
    private val log = getLogger(javaClass)

    private lateinit var actor: ParkingOperatorActor

    init {
        eventExecutionStrategy.execute {
            actor = loadParkingSpotOperator()
        }
    }

    suspend fun handleCarArrived(
        gateId: GateId,
        plateNumber: PlateNumber,
        hasDisabilityCard: Boolean,
    ) {
        if (!isPlateRegistered(plateNumber)) {
            gateController.showError(gateId, plateNumber, GateResponse.Error.PlateNumberNotRegisteredError)
            return
        }

        val response = actor.execute { onVehicleArrival(gateId, plateNumber, hasDisabilityCard) }

        handleGateResponse(response, gateId, plateNumber)
    }

    private suspend fun handleGateResponse(
        response: GateResponse,
        gateId: GateId,
        plateNumber: PlateNumber,
    ) {
        when (response) {
            is GateResponse.Action.LetVehicleOut -> gateController.openGate(gateId, plateNumber)
            is GateResponse.Action.ProvideParkingSpot -> {
                gateController.showProvidedParkingSpot(gateId, response.parkingSpotId, plateNumber)
            }

            is GateResponse.Error -> gateController.showError(gateId, plateNumber, response)
        }
    }

    suspend fun importEvent(event: BaseEvent) = actor.execute { apply(event.asImported()) }

    private suspend fun isPlateRegistered(plateNumber: PlateNumber) = customerPort.isPlateNumberRegistered(plateNumber)

    suspend fun handleCarDroveThrough(
        gateId: GateId,
        plateNumber: PlateNumber,
    ) = actor.execute { onVehicleDroveThrough(gateId, plateNumber) }

    suspend fun handleCarParkedOff(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) = actor.execute { onVehicleParkedOff(parkingSpotId, plateNumber) }

    suspend fun handleCarParkedOn(
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    ) = actor.execute { onVehicleParkedOn(parkingSpotId, plateNumber) }

    private suspend fun loadParkingSpotOperator() =
        try {
            val aggregate = store.load(PARKING_SPOT_OPERATOR_AGGREGATE_ID, ParkingOperatorAggregate::class.java)
            ParkingOperatorActor(aggregate, store)
        } catch (_: AggregateNotFoundException) {
            log.info("Parking spot operator aggregate was not found. Creating a new parking spot operator.")
            ParkingOperatorActor(ParkingOperatorAggregate(PARKING_SPOT_OPERATOR_AGGREGATE_ID), store)
        }

    fun clearCache() {
        actor = ParkingOperatorActor(ParkingOperatorAggregate(PARKING_SPOT_OPERATOR_AGGREGATE_ID), store)
    }

    companion object {
        const val PARKING_SPOT_OPERATOR_AGGREGATE_ID = "parking-spot-operator-aggregate-id"
    }
}

@Component
class ParkingOperationListener(
    private val eventExecutionStrategy: EventExecutionStrategy,
    private val notificationPort: CustomerNotificationPort,
) {
    @EventListener(ParkingSpotReprovidedEvent::class)
    fun onEvent(event: ParkingSpotReprovidedEvent) {
        eventExecutionStrategy.execute {
            notificationPort.notify(event)
        }
    }
}

class ParkingOperatorActor(
    private val aggregate: ParkingOperatorAggregate,
    private val aggregateStore: AggregateStore,
) {
    private val commandChannel = Channel<suspend () -> Unit>(Channel.UNLIMITED)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch {
            for (cmd in commandChannel) {
                cmd()
            }
        }
    }

    suspend fun <T> execute(command: suspend ParkingOperatorAggregate.() -> T): T {
        val deferred = CompletableDeferred<T>()
        commandChannel.send {
            try {
                val result = aggregate.command()
                aggregateStore.save(aggregate)
                deferred.complete(result)
            } catch (e: Throwable) {
                deferred.completeExceptionally(e)
            }
        }
        return deferred.await()
    }
}

fun interface CustomerNotificationPort {
    suspend fun notify(event: ParkingSpotReprovidedEvent)
}

interface GateControllerPort {
    suspend fun showError(
        gateId: GateId,
        plateNumber: PlateNumber,
        error: GateResponse.Error,
    )

    suspend fun showProvidedParkingSpot(
        gateId: GateId,
        parkingSpotId: ParkingSpotId,
        plateNumber: PlateNumber,
    )

    suspend fun openGate(
        gateId: GateId,
        plateNumber: PlateNumber,
    )
}

fun interface CustomerOperationApiPort {
    suspend fun isPlateNumberRegistered(plateNumber: PlateNumber): Boolean
}
