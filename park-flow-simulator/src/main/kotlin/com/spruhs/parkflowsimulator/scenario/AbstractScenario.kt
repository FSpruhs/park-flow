package com.spruhs.parkflowsimulator.scenario

import com.spruhs.parkflowsimulator.getLogger
import com.spruhs.parkflowsimulator.publisher.VehicleEventPublisher
import com.spruhs.parkflowsimulator.webclient.ParkFlowWebClientService
import com.spruhs.parksensormock.events.CarArrivedSensorEvent
import com.spruhs.parksensormock.events.CarDroveThroughSensorEvent
import com.spruhs.parksensormock.events.CarParkedOffSensorEvent
import com.spruhs.parksensormock.events.CarParkedOnSensorEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.Instant
import kotlin.random.Random

abstract class Scenario(
    protected val webClient: ParkFlowWebClientService,
    protected val vehicleEventPublisher: VehicleEventPublisher? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    protected val log = getLogger(javaClass)

    private val rnd = Random(42)
    private val gateQueues = mutableMapOf<String, Pair<Channel<VehicleSimulation>, Channel<Unit>>>()
    private val vehicleChannels = mutableMapOf<String, Channel<VehicleAction>>()
    private val jobs = mutableListOf<Job>()
    private val queueJobs = mutableListOf<Job>()
    private val simulationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    protected val parkingSpotsIdLookup: MutableMap<String, String> = mutableMapOf()
    protected val gatesIdLookup: MutableMap<String, String> = mutableMapOf()
    protected val customersIdLookup: MutableMap<String, String> = mutableMapOf()

    suspend fun openEntranceGate(plateNumber: String, parkingSpotId: String) {
        vehicleChannels[plateNumber]?.send(VehicleAction.DroveThroughEntrance(parkingSpotId))
    }

    suspend fun openExitGate(plateNumber: String) {
        vehicleChannels[plateNumber]?.send(VehicleAction.DroveThroughExit)
    }

    suspend fun errorGate(plateNumber: String) {
        vehicleChannels[plateNumber]?.send(VehicleAction.GateError)
    }

    fun createGateQueue(name: String) {
        gateQueues[name] = Channel<VehicleSimulation>(Channel.UNLIMITED) to Channel()
    }

    suspend fun enqueue(name: String, item: VehicleSimulation) {
        gateQueues[name]?.first?.send(item)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun processVehicle(vehicle: VehicleSimulation) {
        sendVehicleArrived(vehicle.entrance.id(), vehicle.plateNumber, vehicle.hasDisabilityCard)
        val receivedEntrance = vehicleChannels[vehicle.plateNumber]?.receive()
        if (receivedEntrance == null) {
            log.error("Vehicle ${vehicle.plateNumber} not found")
            return
        }

        when (receivedEntrance) {
            is VehicleAction.DroveThroughEntrance -> sendDroveThroughEntrance(
                vehicle.entrance.id(),
                vehicle.plateNumber,
                vehicle.gateQueueName
            )

            else -> {
                log.error("Vehicle ${vehicle.plateNumber} received unexpected action ${receivedEntrance::class.simpleName}")
                vehicleChannels[vehicle.plateNumber]?.close()
                vehicleChannels.remove(vehicle.plateNumber)
                gateQueues[vehicle.gateQueueName]?.second?.send(Unit)
                return
            }
        }

        val action = select {
            vehicleChannels[vehicle.plateNumber]?.onReceive { it }
            onTimeout(vehicle.parkOnDelay) { null }
        }

        when (action) {
            null -> {
                if (vehicle.parkeOnWrongParkingSpot != null) {
                    sendParkedOn(vehicle.parkeOnWrongParkingSpot.id(), vehicle.plateNumber)
                    delay(vehicle.parkOffDelay)
                    sendParkedOff(vehicle.parkeOnWrongParkingSpot.id(), vehicle.plateNumber)
                } else {
                    sendParkedOn(receivedEntrance.providedParkingSpot, vehicle.plateNumber)
                    delay(vehicle.parkOffDelay)
                    sendParkedOff(receivedEntrance.providedParkingSpot, vehicle.plateNumber)
                }

            }

            is VehicleAction.ReprovideParkingSpot -> {
                sendParkedOn(action.parkingSpotId, vehicle.plateNumber)
                delay(vehicle.parkOffDelay)
                sendParkedOff(action.parkingSpotId, vehicle.plateNumber)
            }

            else -> {
                log.error("Vehicle ${vehicle.plateNumber} received unexpected action ${action::class.simpleName}")
            }
        }

        delay(vehicle.exitDelay)
        sendVehicleArrived(vehicle.exit.id(), vehicle.plateNumber, false)
        vehicleChannels[vehicle.plateNumber]?.receive()

        sendDroveThroughExit(vehicle.exit.id(), vehicle.plateNumber)
        vehicleChannels[vehicle.plateNumber]?.close()
        vehicleChannels.remove(vehicle.plateNumber)
    }

    fun startProcessing(
        name: String,
        intervalProvider: suspend () -> Long,
        handler: suspend (VehicleSimulation) -> Unit
    ) {
        val gateQueue = gateQueues[name] ?: error("Queue not found")
        log.info("Start processing queue $name")

        val job = simulationScope.launch {
            var intervalMs: Long
            for (vehicle in gateQueue.first) {
                val plateChannel = Channel<VehicleAction>(capacity = Channel.BUFFERED)
                vehicleChannels[vehicle.plateNumber] = plateChannel
                val job = launch {
                    handler(vehicle)
                }

                jobs.add(job)

                gateQueue.second.receive()
                intervalMs = intervalProvider()
                delay(intervalMs)
            }
        }
        queueJobs.add(job)
    }

    fun closeQueue(name: String) {
        gateQueues[name]?.first?.close()
    }

    suspend fun joinAllJobs() {
        queueJobs.joinAll()
        jobs.joinAll()
    }

    suspend fun sendVehicleArrived(gateId: String, plateNumber: String, hasDisabilityCard: Boolean = false) {
        vehicleEventPublisher?.sendArrived(
            CarArrivedSensorEvent(
                gateId,
                plateNumber,
                hasDisabilityCard
            )
        )
        log.info("$plateNumber arrived")
    }

    suspend fun sendDroveThroughEntrance(gateId: String, plateNumber: String, gateQueueName: String) {
        sendDroveThrough(gateId, plateNumber)
        gateQueues[gateQueueName]?.second?.send(Unit)
    }

    suspend fun sendDroveThroughExit(gateId: String, plateNumber: String) {
        sendDroveThrough(gateId, plateNumber)
    }

    suspend fun sendDroveThrough(gateId: String, plateNumber: String) {
        vehicleEventPublisher?.sendDroveThrough(
            CarDroveThroughSensorEvent(
                gateId,
                plateNumber
            )
        )
        log.info("$plateNumber drove through")
    }

    suspend fun sendParkedOn(parkingSpotId: String, plateNumber: String) {
        vehicleEventPublisher?.sendParkedOn(
            CarParkedOnSensorEvent(
                parkingSpotId,
                plateNumber
            )
        )
        log.info("$plateNumber parked on")
    }

    suspend fun sendParkedOff(parkingSpotId: String, plateNumber: String) {
        vehicleEventPublisher?.sendParkedOff(
            CarParkedOffSensorEvent(
                parkingSpotId,
                plateNumber
            )
        )
        log.info("$plateNumber parked off")
    }

    protected suspend fun createParkingSpot(parkingSpot: ParkingSpotInfo) {
        log.info("Create parking spot: ${parkingSpot.name}")
        parkingSpotsIdLookup[parkingSpot.name] = webClient.createParkingSpot(parkingSpot)
        log.info("Created parking spot: ${parkingSpot.name}")
    }

    protected suspend fun createGate(gate: GateInfo) {
        log.info("Create gate: ${gate.name}")
        gatesIdLookup[gate.name] = webClient.createGate(gate)
        log.info("Created gate: ${gate.name}")
    }

    protected suspend fun activateGate(gate: GateInfo) {
        log.info("Activate gate: ${gate.name}")
        webClient.activateGate(gate.id())
        log.info("Activated gate: ${gate.name}")
    }

    protected suspend fun deactivateGate(gate: GateInfo) {
        log.info("Deactivate gate: ${gate.name}")
        webClient.deactivateGate(gate.id())
        log.info("Deactivated gate: ${gate.name}")
    }

    protected suspend fun removeGate(gate: GateInfo) {
        log.info("Remove gate: ${gate.name}")
        webClient.removeGate(gate.id())
        log.info("Removed gate: ${gate.name}")
    }

    protected suspend fun renameParkingSpot(parkingSpot: ParkingSpotInfo, newName: String) {
        log.info("Rename parking spot: ${parkingSpot.name} to $newName")
        webClient.renameParkingSpot(parkingSpot.id(), newName)
        log.info("Renamed parking spot: ${parkingSpot.name} to $newName")
    }

    protected suspend fun addParkingSpotType(parkingSpot: ParkingSpotInfo, types: List<String>, price: String? = null) {
        log.info("Add $types to parking spot: ${parkingSpot.name}")
        webClient.addParkingSpotType(parkingSpot.id(), types, price)
        log.info("Added $types to parking spot: ${parkingSpot.name}")
    }

    protected suspend fun removeParkingSpotType(parkingSpot: ParkingSpotInfo, types: List<String>) {
        log.info("Remove $types to parking spot: ${parkingSpot.name}")
        webClient.removeParkingSpotType(parkingSpot.id(), types)
        log.info("Removed $types to parking spot: ${parkingSpot.name}")
    }

    protected suspend fun activateParkingSpot(parkingSpot: ParkingSpotInfo) {
        log.info("Activate parking spot: ${parkingSpot.name}")
        webClient.activateParkingSpot(parkingSpot.id())
        log.info("Activated parking spot: ${parkingSpot.name}")
    }

    protected suspend fun deactivateParkingSpot(parkingSpot: ParkingSpotInfo) {
        log.info("Deactivate parking spot: ${parkingSpot.name}")
        webClient.deactivateParkingSpot(parkingSpot.id())
        log.info("Deactivated parking spot: ${parkingSpot.name}")
    }

    protected suspend fun removeParkingSpot(parkingSpot: ParkingSpotInfo) {
        log.info("Remove parking spot: ${parkingSpot.name}")
        webClient.removeParkingSpot(parkingSpot.id())
        log.info("Removed parking spot: ${parkingSpot.name}")
    }

    protected suspend fun createCustomer(customer: CustomerInfo) {
        log.info("Create customer: ${customer.plateNumber}")
        customersIdLookup[customer.plateNumber] = webClient.createCustomer(customer)
        log.info("Created customer: ${customer.plateNumber}")
    }

    protected suspend fun changePaymentMethod(customer: CustomerInfo, paymentMethodId: String) {
        log.info("Change payment method id: $paymentMethodId")
        webClient.changePaymentMethod(customer.id(), paymentMethodId)
        log.info("Changed payment method id: $paymentMethodId")
    }

    protected suspend fun addVehicle(customer: CustomerInfo, plateNumber: String) {
        log.info("Add vehicle: $plateNumber")
        webClient.addVehicle(customer.id(), plateNumber)
        customersIdLookup[plateNumber] = customer.id()
        log.info("Added vehicle: $plateNumber")
    }

    protected suspend fun removeVehicle(customer: CustomerInfo, plateNumber: String) {
        log.info("Remove vehicle: $plateNumber")
        webClient.removeVehicle(customer.id(), plateNumber)
        log.info("Removed vehicle: $plateNumber")
    }

    protected suspend fun rentParkingSpot(customer: CustomerInfo, parkingSpot: ParkingSpotInfo, plateNumber: String) {
        log.info("Rent parking spot: ${parkingSpot.id()}")
        webClient.rentParkingSpot(customer.id(), plateNumber, parkingSpot.id())
        log.info("Rented parking spot: ${parkingSpot.id()}")
    }

    protected suspend fun cancelParkingSpot(customer: CustomerInfo, parkingSpot: ParkingSpotInfo) {
        log.info("Cancel parking spot: ${parkingSpot.id()}")
        webClient.cancelParkingSpot(customer.id(), parkingSpot.id())
        log.info("Canceled parking spot: ${parkingSpot.id()}")
    }


    protected suspend fun <T> expectHttpError(
        expectedCode: Int,
        block: suspend () -> T
    ) {
        try {
            block()
            error("expected to fail with http $expectedCode but it succeeded!")
        } catch (ex: WebClientResponseException) {
            if (ex.statusCode.value() != expectedCode) {
                log.error("Wrong error code. expected=$expectedCode actual=${ex.statusCode.value()}")
            }
            log.info("$expectedCode confirmed")
        }
    }

    protected suspend fun runActions(actions: List<ScenarioAction>, delay: Long = 100) = coroutineScope {
        val actionJobs = actions.map { action ->
            launch {
                when (action) {
                    is ScenarioAction.CreateParkingSpot -> createParkingSpot(action.info)
                    is ScenarioAction.CreateGate -> createGate(action.info)
                    is ScenarioAction.CreateCustomer -> createCustomer(action.info)
                    is ScenarioAction.ExpectError -> expectHttpError(action.expected) { action.block() }
                    is ScenarioAction.Custom -> action.block()
                }
            }.also { delay(delay) }
        }
        jobs.addAll(actionJobs)
    }

    protected suspend fun runAction(action: ScenarioAction) {
        runActions(listOf(action))
    }

    protected abstract suspend fun start()

    protected fun GateInfo.id() = gatesIdLookup[name] ?: ""

    protected fun ParkingSpotInfo.id() = parkingSpotsIdLookup[name] ?: ""

    protected fun CustomerInfo.id() = customersIdLookup[plateNumber] ?: ""

    fun run(): Job = CoroutineScope(dispatcher).launch {
        start()
    }

    protected suspend fun getVehicleHistory(plateNumber: String): VehicleHistoryReflection {
        return webClient.getVehicleHistory(plateNumber)
    }

    suspend fun notifyReprovideParkingSpot(plateNumber: String, parkingSpotId: String) {
        vehicleChannels[plateNumber]?.send(VehicleAction.ReprovideParkingSpot(parkingSpotId))
    }

    protected fun randomDelayMinuets(minMinutes: Double = 1.0, maxMinutes: Double = 3.0): Long {
        require(minMinutes in 0.0..maxMinutes) { "minMinutes must be <= maxMinutes and >= 0" }
        val minutes = rnd.nextDouble(minMinutes, maxMinutes)
        return (minutes * 60_000.0).toLong()
    }

    protected fun buildGateQueueNames(gate: GateInfo, count: Int): List<String> =
        List(count) { index -> "${index + 1}-${gate.id()}" }

    protected fun createPlate(i: Int, electricRate: Int, disabledRate: Int): Pair<String, Boolean> {
        var plate = "K-A$i"
        val disabled = i % disabledRate == 0
        if (i % electricRate == 0) plate += "E"
        return plate to disabled
    }

    protected suspend fun createGenericParkingSpots(
        numberOfParkingSpots: Int,
        electricRate: Int = 10,
        disabledRate: Int = 25,
        rentableRate: Int = 33
    ) {
        repeat(numberOfParkingSpots) { i ->
            val types = buildList {
                if (i % electricRate == 0) add("ELECTRIC")
                if (i % disabledRate == 0) add("DISABLED")
                if (i % rentableRate == 0) add("RENTABLE")
            }

            val price = if (i % rentableRate == 0) "$i.10" else null

            runAction(
                ScenarioAction.CreateParkingSpot(
                    ParkingSpotInfo("P-$i", types, price)
                )
            )
        }
    }

    protected suspend fun validateGenericHistory(numberOfHistories: Int) {
        repeat(numberOfHistories) { i ->
            var plateNumber = "K-A$i"
            if (i % 10 == 0) {
                plateNumber += "E"
            }
            val history = getVehicleHistory(plateNumber)

            val historyValidator = VehicleHistoryValidator(
                plateNumber,
                6,
                mapOf(
                    HistoryType.CREATED to 1,
                    HistoryType.ENTER to 1,
                    HistoryType.PARKED_ON_CORRECT to 1,
                    HistoryType.PARKED_OFF to 1,
                    HistoryType.EXIT to 1,
                    HistoryType.INVOICED to 1
                ),
                expectedPrice = "10"
            )
            validate(history, historyValidator)
        }
    }

    protected suspend fun createGenericVehicles(
        numberOfVehicles: Int,
        parkOnDelay: Pair<Double, Double> = 1.0 to 2.0,
        parkOffDelay: Pair<Double, Double> = 10.0 to 20.0,
        exitDelay: Pair<Double, Double> = 1.0 to 2.0,
        electricRate: Int = 10,
        disabledRate: Int = 33,
        queueSelector: (vehicleIndex: Int) -> Triple<GateInfo, GateInfo, String>,
    ) {
        repeat(numberOfVehicles) { i ->
            val (plateNumber, hasDisabilityCard) = createPlate(i, electricRate, disabledRate)

            runAction(ScenarioAction.CreateCustomer(CustomerInfo(plateNumber, hasDisabilityCard = hasDisabilityCard)))

            val (entrance, exit, queueName) = queueSelector(i)

            enqueue(
                queueName,
                VehicleSimulation(
                    plateNumber,
                    entrance,
                    exit,
                    queueName,
                    randomDelayMinuets(parkOnDelay.first, parkOnDelay.second),
                    randomDelayMinuets(parkOffDelay.first, parkOffDelay.second),
                    randomDelayMinuets(exitDelay.first, exitDelay.second),
                    hasDisabilityCard
                )
            )
        }
    }

    fun minutesToMillis(minutes: Int): Long =
        minutes * 60_000L

    fun secondsToMillis(seconds: Int): Long =
        seconds * 1_000L
}

sealed class ScenarioAction {
    data class CreateParkingSpot(val info: ParkingSpotInfo) : ScenarioAction()
    data class CreateGate(val info: GateInfo) : ScenarioAction()
    data class CreateCustomer(val info: CustomerInfo) : ScenarioAction()
    data class ExpectError(val expected: Int, val block: suspend () -> Unit) : ScenarioAction()
    data class Custom(val block: suspend () -> Unit) : ScenarioAction()
}

data class ParkingSpotInfo(
    val name: String,
    val types: List<String> = emptyList(),
    val price: String? = null
)

data class GateInfo(
    val name: String,
    val type: String
)

data class CustomerInfo(
    val plateNumber: String,
    val paymentMethodId: String = "Paypal",
    val hasDisabilityCard: Boolean = false
)

data class VehicleSimulation(
    val plateNumber: String,
    val entrance: GateInfo,
    val exit: GateInfo,
    val gateQueueName: String,
    val parkOnDelay: Long = 0,
    val parkOffDelay: Long = 0,
    val exitDelay: Long = 0,
    val hasDisabilityCard: Boolean = false,
    val parkeOnWrongParkingSpot: ParkingSpotInfo? = null
)

sealed class VehicleAction {
    data class DroveThroughEntrance(val providedParkingSpot: String) : VehicleAction()
    data class ReprovideParkingSpot(val parkingSpotId: String) : VehicleAction()
    object DroveThroughExit : VehicleAction()
    object GateError : VehicleAction()
}

data class VehicleHistoryReflection(
    val plateNumber: String,
    val customerId: String,
    val history: List<HistoryItem>
)

data class HistoryItem(
    val time: Instant,
    val type: HistoryType,
    val parkingSpotId: String? = null,
    val amount: String? = null
)

enum class HistoryType {
    CREATED,
    ENTER,
    PARKED_ON_CORRECT,
    PARKED_ON_WRONG,
    PARKED_OFF,
    EXIT,
    REMOVED,
    INVOICED,
    PAYED
}
