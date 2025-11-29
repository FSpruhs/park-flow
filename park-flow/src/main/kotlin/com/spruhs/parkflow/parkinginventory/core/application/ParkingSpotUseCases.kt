package com.spruhs.parkflow.parkinginventory.core.application

import com.spruhs.parkflow.common.es.AggregateNotFoundException
import com.spruhs.parkflow.common.es.AggregateStore
import com.spruhs.parkflow.common.helper.KeyedMutex
import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import com.spruhs.parkflow.parkinginventory.api.Price
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotAggregate
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotName
import com.spruhs.parkflow.parkinginventory.core.domain.ParkingSpotNotFoundException
import org.springframework.stereotype.Component

@Component
class ParkingSpotCommandPort(
    private val aggregateStore: AggregateStore,
    private val parkingInventoryService: ParkingInventoryService,
) {
    private val log = getLogger(javaClass)
    private val mutex = KeyedMutex<ParkingSpotId>()

    suspend fun create(command: CreateParkingSpotCommand): String {
        parkingInventoryService.reserveParkingSpotName(command.parkingSpotName)

        return ParkingSpotAggregate.create(command.parkingSpotName, command.parkingSpotTypes, command.price)
            .also { aggregateStore.save(it) }
            .aggregateId
    }

    suspend fun activate(id: ParkingSpotId) = handle(id) { it.activate() }

    suspend fun deactivate(id: ParkingSpotId) = handle(id) { it.deactivate() }

    suspend fun remove(id: ParkingSpotId) = handle(id) { it.remove() }

    suspend fun rename(
        id: ParkingSpotId,
        newName: ParkingSpotName,
    ) {
        parkingInventoryService.reserveParkingSpotName(newName)

        handle(id) { it.rename(newName) }
    }

    suspend fun addTypes(command: AddParkingSpotTypeCommand) =
        handle(command.parkingSpotId) { it.addTypes(command.types, command.price) }

    suspend fun removeTypes(
        id: ParkingSpotId,
        types: Set<ParkingSpotType>,
    ) = handle(id) { it.removeTypes(types) }

    private suspend inline fun handle(
        id: ParkingSpotId,
        crossinline block: (ParkingSpotAggregate) -> Unit,
    ) {
        mutex.withKeyLock(id) {
            loadParkingSpot(id).also {
                block(it)
                aggregateStore.save(it)
            }
        }
    }

    private suspend fun loadParkingSpot(id: ParkingSpotId): ParkingSpotAggregate =
        try {
            aggregateStore.load(id.value, ParkingSpotAggregate::class.java)
        } catch (e: AggregateNotFoundException) {
            log.error(e.message)
            throw ParkingSpotNotFoundException(id)
        }
}

data class CreateParkingSpotCommand(
    val parkingSpotName: ParkingSpotName,
    val parkingSpotTypes: Set<ParkingSpotType>,
    val price: Price? = null,
)

data class AddParkingSpotTypeCommand(
    val parkingSpotId: ParkingSpotId,
    val types: Set<ParkingSpotType>,
    val price: Price? = null,
)
