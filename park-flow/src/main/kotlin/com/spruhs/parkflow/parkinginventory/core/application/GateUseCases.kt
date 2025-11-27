package com.spruhs.parkflow.parkinginventory.core.application

import com.spruhs.parkflow.common.es.AggregateNotFoundException
import com.spruhs.parkflow.common.es.AggregateStore
import com.spruhs.parkflow.common.helper.getLogger
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.GateType
import com.spruhs.parkflow.parkinginventory.core.domain.GateAggregate
import com.spruhs.parkflow.parkinginventory.core.domain.GateName
import com.spruhs.parkflow.parkinginventory.core.domain.GateNotFoundException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component

@Component
class GateCommandPort(
    private val aggregateStore: AggregateStore,
    private val parkingInventoryService: ParkingInventoryService,
) {
    private val log = getLogger(javaClass)
    private val mutex = Mutex()

    suspend fun create(command: CreateGateCommand): String {
        parkingInventoryService.reserveGateName(command.gateName)
        return GateAggregate.create(command.gateType, command.gateName)
            .also { aggregateStore.save(it) }
            .aggregateId
    }

    suspend fun activate(id: GateId) = handle(id) { it.activate() }

    suspend fun deactivate(id: GateId) = handle(id) { it.deactivate() }

    suspend fun remove(id: GateId) = handle(id) { it.remove() }

    private suspend inline fun handle(
        id: GateId,
        block: (GateAggregate) -> Unit,
    ) {
        mutex.withLock {
            loadGate(id).also {
                block(it)
                aggregateStore.save(it)
            }
        }
    }

    private suspend fun loadGate(id: GateId): GateAggregate =
        try {
            aggregateStore.load(id.value, GateAggregate::class.java)
        } catch (e: AggregateNotFoundException) {
            log.error(e.message)
            throw GateNotFoundException(id)
        }
}

data class CreateGateCommand(
    val gateName: GateName,
    val gateType: GateType,
)
