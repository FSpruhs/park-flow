package com.spruhs.parkflow.parkinginventory.core.domain

import com.spruhs.parkflow.common.es.AggregateRoot
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.common.helper.generateId
import com.spruhs.parkflow.parkinginventory.api.GateActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.GateId
import com.spruhs.parkflow.parkinginventory.api.GateRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.GateType

class GateAggregate(override val aggregateId: String) : AggregateRoot(aggregateId, TYPE) {
    var gateType: GateType = GateType.ENTRANCE
    var name: GateName = GateName("DEFAULT")
    var activationState: ActivationState = ActivationState.ACTIVE
    var removed: Boolean = false

    override fun whenEvent(event: BaseEvent) {
        when (event) {
            is GateCreatedEvent -> handleGateCreatedEvent(event)
            is GateActivatedEvent -> this.activationState = ActivationState.ACTIVE
            is GateDeactivatedEvent -> this.activationState = ActivationState.INACTIVE
            is GateRemovedEvent -> this.removed = true

            else -> UnknownEventTypeException(event)
        }
    }

    private fun handleGateCreatedEvent(event: GateCreatedEvent) {
        this.name = event.name
        this.gateType = event.gateType
    }

    private fun ensureNotRemoved() {
        require(!removed) { "ParkingSpot has been removed and cannot accept commands anymore." }
    }

    fun activate() {
        ensureNotRemoved()
        if (activationState == ActivationState.ACTIVE) return

        apply(GateActivatedEvent(aggregateId))
    }

    fun deactivate() {
        ensureNotRemoved()
        if (activationState == ActivationState.INACTIVE) return

        apply(GateDeactivatedEvent(aggregateId))
    }

    fun remove() {
        ensureNotRemoved()

        apply(GateRemovedEvent(aggregateId))
    }

    companion object {
        const val TYPE = "Gate"

        fun create(
            gateType: GateType,
            name: GateName,
        ) = GateAggregate(generateId())
            .also { it.apply(GateCreatedEvent(it.aggregateId, gateType, name)) }
    }
}

@JvmInline
value class GateName(val value: String) {
    init {
        require(value.isNotBlank()) { "Gate name cannot be empty" }
    }
}

data class GateNotFoundException(val id: GateId) : RuntimeException("Gate not found: ${id.value}")
