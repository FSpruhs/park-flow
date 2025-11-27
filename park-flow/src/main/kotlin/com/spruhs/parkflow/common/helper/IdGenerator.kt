package com.spruhs.parkflow.common.helper

import java.util.UUID

fun generateId(): String {
    return UUID.randomUUID().toString()
}
