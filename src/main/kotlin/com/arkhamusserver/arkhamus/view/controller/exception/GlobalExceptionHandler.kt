package com.arkhamusserver.arkhamus.view.controller.exception

import com.arkhamusserver.arkhamus.logic.exception.ArkhamusServerRequestException
import com.arkhamusserver.arkhamus.view.validator.ArkhamusValidationException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime


@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler {
    @ExceptionHandler(ArkhamusValidationException::class)
    fun handleValidationException(
        e: ArkhamusValidationException,
        request: WebRequest
    ): ResponseEntity<Map<String, String>> {
        val body: MutableMap<String, String> = LinkedHashMap()
        body["timestamp"] = LocalDateTime.now().toString()
        body["message"] = e.message?:"no message"
        body["relatedEntity"] = e.relatedEntity
        return ResponseEntity<Map<String, String>>(body, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(ArkhamusServerRequestException::class)
    fun handleArkhamusServerRequestException(
        e: ArkhamusServerRequestException,
        request: WebRequest
    ): ResponseEntity<Map<String, String>> {
        val body: MutableMap<String, String> = LinkedHashMap()
        body["timestamp"] = LocalDateTime.now().toString()
        body["message"] = e.message?:"no message"
        body["relatedEntity"] = e.relatedEntity
        return ResponseEntity<Map<String, String>>(body, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(
        e: UsernameNotFoundException,
        request: WebRequest
    ): ResponseEntity<Map<String, String>> {
        val body: MutableMap<String, String> = LinkedHashMap()
        body["timestamp"] = LocalDateTime.now().toString()
        body["message"] = e.message?:"no message"
        return ResponseEntity<Map<String, String>>(body, HttpStatus.FORBIDDEN)
    }
}