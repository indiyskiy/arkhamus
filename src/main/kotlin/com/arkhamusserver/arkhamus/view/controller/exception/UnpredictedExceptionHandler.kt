package com.arkhamusserver.arkhamus.view.controller.exception

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime


@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class UnpredictedExceptionHandler {
    @ExceptionHandler(Throwable::class)
    fun handleValidationException(
        e: Throwable,
        request: WebRequest
    ): ResponseEntity<Map<String, String>> {
        val body: MutableMap<String, String> = LinkedHashMap()
        body["timestamp"] = LocalDateTime.now().toString()
        body["message"] = e.message ?: "no message"
        body["trace"] = e.stackTraceToString()
        return ResponseEntity<Map<String, String>>(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}