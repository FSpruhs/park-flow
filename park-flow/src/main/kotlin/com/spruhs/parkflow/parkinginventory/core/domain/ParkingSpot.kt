package com.spruhs.parkflow.parkinginventory.core.domain

import com.spruhs.parkflow.common.es.AggregateRoot
import com.spruhs.parkflow.common.es.BaseEvent
import com.spruhs.parkflow.common.es.UnknownEventTypeException
import com.spruhs.parkflow.common.helper.generateId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotActivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotCreatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotDeactivatedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotId
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotRenamedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotType
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesAddedEvent
import com.spruhs.parkflow.parkinginventory.api.ParkingSpotTypesRemovedEvent
import com.spruhs.parkflow.parkinginventory.api.Price

class ParkingSpotAggregate(override val aggregateId: String) : AggregateRoot(aggregateId, TYPE) {
    var name: ParkingSpotName = ParkingSpotName("A-0")
    var activationState: ActivationState = ActivationState.ACTIVE
    val spotTypes: MutableSet<ParkingSpotType> = mutableSetOf()
    var removed: Boolean = false
    var price: Price? = null

    override fun whenEvent(event: BaseEvent) {
        when (event) {
            is ParkingSpotCreatedEvent -> handleParkingSpotCreatedEvent(event)
            is ParkingSpotRemovedEvent -> removed = true
            is ParkingSpotTypesRemovedEvent -> handleTypesRemovedEvent(event)
            is ParkingSpotTypesAddedEvent -> handleTypesAddedEvent(event)
            is ParkingSpotRenamedEvent -> this.name = event.newName
            is ParkingSpotActivatedEvent -> this.activationState = ActivationState.ACTIVE
            is ParkingSpotDeactivatedEvent -> this.activationState = ActivationState.INACTIVE

            else -> throw UnknownEventTypeException(event)
        }
    }

    private fun handleTypesAddedEvent(event: ParkingSpotTypesAddedEvent) {
        event.types.forEach { spotTypes.add(it) }
        event.price?.let { price = it }
    }

    private fun handleTypesRemovedEvent(event: ParkingSpotTypesRemovedEvent) {
        event.types.forEach { spotTypes.remove(it) }
        if (ParkingSpotType.Rentable in event.types) this.price = null
    }

    private fun handleParkingSpotCreatedEvent(event: ParkingSpotCreatedEvent) {
        this.name = event.parkingSpotName
        this.activationState = event.spotState
        this.spotTypes.addAll(event.spotTypes)
        event.price?.let { price = it }
    }

    private fun ensureNotRemoved() {
        require(!removed) { "ParkingSpot has been removed and cannot accept commands anymore." }
    }

    fun activate() {
        ensureNotRemoved()
        if (this.activationState == ActivationState.ACTIVE) return

        apply(ParkingSpotActivatedEvent(this.aggregateId))
    }

    fun deactivate() {
        ensureNotRemoved()
        if (this.activationState == ActivationState.INACTIVE) return

        apply(ParkingSpotDeactivatedEvent(this.aggregateId))
    }

    fun remove() {
        ensureNotRemoved()

        apply(ParkingSpotRemovedEvent(this.aggregateId, this.name))
    }

    fun rename(newName: ParkingSpotName) {
        ensureNotRemoved()
        if (this.name == newName) return

        apply(ParkingSpotRenamedEvent(this.aggregateId, newName, this.name))
    }

    fun addTypes(
        types: Set<ParkingSpotType>,
        price: Price? = null,
    ) {
        ensureNotRemoved()

        val typesToAdd = types.subtract(spotTypes)
        if (typesToAdd.isEmpty()) return

        ensureNoInvalidCombination(typesToAdd)
        ensurePriceIfRentable(typesToAdd, price)

        apply(ParkingSpotTypesAddedEvent(this.aggregateId, typesToAdd, price))
    }

    fun removeTypes(types: Set<ParkingSpotType>) {
        ensureNotRemoved()

        val typesToRemove = types.filter { it in spotTypes }.toSet()
        if (typesToRemove.isEmpty()) return

        apply(ParkingSpotTypesRemovedEvent(this.aggregateId, typesToRemove))
    }

    private fun ensureNoInvalidCombination(typesToAdd: Set<ParkingSpotType>) {
        val addsDisabled = ParkingSpotType.Disabled in typesToAdd
        val addsRentable = ParkingSpotType.Rentable in typesToAdd

        val hasDisabled = ParkingSpotType.Disabled in spotTypes
        val hasRentable = ParkingSpotType.Rentable in spotTypes

        if ((addsDisabled && hasRentable) || (addsRentable && hasDisabled)) {
            throw DisabledParkingSpotsNotRentableException()
        }
    }

    private fun ensurePriceIfRentable(
        typesToAdd: Set<ParkingSpotType>,
        price: Price?,
    ) {
        if (ParkingSpotType.Rentable in typesToAdd && price == null) {
            throw RentableParkingSpotWithoutPriceException()
        }
    }

    companion object {
        const val TYPE = "ParkingSpot"

        fun create(
            parkingSpotName: ParkingSpotName,
            spotTypes: Set<ParkingSpotType>,
            price: Price? = null,
        ): ParkingSpotAggregate =
            ParkingSpotAggregate(generateId())
                .also {
                    if (ParkingSpotType.Rentable in spotTypes && price == null) {
                        throw RentableParkingSpotWithoutPriceException()
                    }
                }
                .also {
                    it.apply(
                        ParkingSpotCreatedEvent(
                            aggregateId = it.aggregateId,
                            parkingSpotName = parkingSpotName,
                            spotTypes = spotTypes,
                            spotState = ActivationState.ACTIVE,
                            price = price,
                        ),
                    )
                }
    }
}

enum class ActivationState {
    ACTIVE,
    INACTIVE,
}

@JvmInline
value class ParkingSpotName(val value: String) {
    init {
        require(value.matches(Regex("^[A-Z]-\\d+$"))) {
            "ParkingSpot name must match pattern {CapitalLetter}-{Number}, e.g. A-15"
        }
    }
}

data class ParkingSpotNotFoundException(val id: ParkingSpotId) :
    RuntimeException("Could not find parking spot: id=${id.value}")

class DisabledParkingSpotsNotRentableException : RuntimeException("Disabled parking spots are not rentable")

class RentableParkingSpotWithoutPriceException : RuntimeException("Rentable parking spots require a price")
