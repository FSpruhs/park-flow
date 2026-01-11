package com.spruhs.parkflow.customeraccess.api

@JvmInline
value class PlateNumber(val value: String) {
    init {
        require(value.matches(Regex("^[A-ZÄÖÜ]{1,3}-[A-Z]{1,2}[0-9]{1,5}(E|H)?$"))) {
            "Invalid German plate number format: $value"
        }
    }

    fun isElectrical(): Boolean = value.last() == 'E'
}

@JvmInline
value class CustomerId(val value: String) {
    init {
        require(value.isNotBlank()) { "Identifier cannot be blank" }
    }
}
