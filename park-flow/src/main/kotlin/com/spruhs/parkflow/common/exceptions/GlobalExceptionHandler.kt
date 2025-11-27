package com.spruhs.parkflow.common.exceptions

import com.spruhs.parkflow.common.helper.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler() {
    private val log = getLogger(javaClass)

    @ExceptionHandler
    fun handleIllegalArgumentException(ex: IllegalArgumentException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.message)
            .also { log.error(ex.message, it) }
}
