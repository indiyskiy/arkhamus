package com.arkhamusserver.arkhamus.view.controller.exception

import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.controller.admin.browser.tech.auth.AuthBrowserController
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    companion object {
        private val logger = LoggingUtils.getLogger<UnpredictedExceptionHandler>()
    }

    @ExceptionHandler(Throwable::class)
    fun handleValidationException(
        e: Throwable,
        request: WebRequest
    ): ResponseEntity<Map<String, String>> {
        logger.error("server fall with unpredicted exception ${e.message}", e)

        val body: MutableMap<String, String> = LinkedHashMap()
        body["timestamp"] = LocalDateTime.now().toString()
        body["message"] = e.message ?: "no message"
        body["trace"] = e.stackTraceToString()
        return ResponseEntity<Map<String, String>>(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}