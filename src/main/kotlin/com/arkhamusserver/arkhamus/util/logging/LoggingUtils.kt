package com.arkhamusserver.arkhamus.util.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.slf4j.event.Level
import java.util.*

/**
 * Utility class for structured logging across the application.
 * Provides methods to add context to logs and standardized logging methods.
 */
object LoggingUtils {
    // MDC key constants
    const val SESSION_ID = "SESSION_ID"
    const val USER_ID = "USER_ID"
    const val GAME_ID = "GAME_ID"
    const val REQUEST_ID = "REQUEST_ID"
    const val EVENT_TYPE = "EVENT_TYPE"

    // Log event types for structured logging
    const val EVENT_GAME_START = "GAME_START"
    const val EVENT_GAME_END = "GAME_END"

    const val EVENT_SYSTEM = "SYSTEM"
    const val EVENT_OUTER_GAME_SYSTEM = "OUTER_GAME_SYSTEM"
    const val EVENT_IN_GAME_SYSTEM = "IN_GAME_SYSTEM"

    const val EVENT_NETTY_SYSTEM = "EVENT_NETTY_SYSTEM"
    const val EVENT_SECURITY = "SECURITY"
    const val EVENT_STEAM = "STEAM"
    const val EVENT_API = "API"

    const val EVENT_PERFORMANCE = "PERFORMANCE"

    /**
     * Get a logger for the specified class.
     */
    inline fun <reified T> getLogger(): Logger = LoggerFactory.getLogger(T::class.java)

    /**
     * Set the session ID in the MDC context.
     */
    fun setSessionId(sessionId: String?) {
        sessionId?.let { MDC.put(SESSION_ID, it) } ?: MDC.remove(SESSION_ID)
    }

    /**
     * Set the user ID in the MDC context.
     */
    fun setUserId(userId: String?) {
        userId?.let { MDC.put(USER_ID, it) } ?: MDC.remove(USER_ID)
    }

    /**
     * Set the game ID in the MDC context.
     */
    fun setGameId(gameId: String?) {
        gameId?.let { MDC.put(GAME_ID, it) } ?: MDC.remove(GAME_ID)
    }

    /**
     * Set the request ID in the MDC context.
     * If no request ID is provided, a new UUID is generated.
     */
    fun setRequestId(requestId: String = UUID.randomUUID().toString()) {
        MDC.put(REQUEST_ID, requestId)
    }

    /**
     * Clear all MDC context values.
     */
    fun clearContext() {
        MDC.clear()
    }

    /**
     * Set the event type in the MDC context.
     */
    fun setEventType(eventType: String) {
        MDC.put(EVENT_TYPE, eventType)
    }

    /**
     * Log a message with structured context.
     */
    fun log(logger: Logger, level: Level, eventType: String, message: String, vararg args: Any) {
        val previousEventType = MDC.get(EVENT_TYPE)
        try {
            MDC.put(EVENT_TYPE, eventType)
            when (level) {
                Level.ERROR -> logger.error(message, *args)
                Level.WARN -> logger.warn(message, *args)
                Level.INFO -> logger.info(message, *args)
                Level.DEBUG -> logger.debug(message, *args)
                Level.TRACE -> logger.trace(message, *args)
            }
        } finally {
            if (previousEventType != null) MDC.put(EVENT_TYPE, previousEventType) else MDC.remove(EVENT_TYPE)
        }
    }

    /**
     * Log an error message with structured context.
     */
    fun error(logger: Logger, eventType: String, message: String, vararg args: Any) {
        log(logger, Level.ERROR, eventType, message, *args)
    }

    /**
     * Log an error message with exception and structured context.
     */
    fun error(logger: Logger, eventType: String, message: String, throwable: Throwable) {
        val previousEventType = MDC.get(EVENT_TYPE)
        try {
            MDC.put(EVENT_TYPE, eventType)
            logger.error(message, throwable)
        } finally {
            if (previousEventType != null) MDC.put(EVENT_TYPE, previousEventType) else MDC.remove(EVENT_TYPE)
        }
    }

    /**
     * Log a warning message with structured context.
     */
    fun warn(logger: Logger, eventType: String, message: String, vararg args: Any) {
        log(logger, Level.WARN, eventType, message, *args)
    }

    /**
     * Log an info message with structured context.
     */
    fun info(logger: Logger, eventType: String, message: String, vararg args: Any) {
        log(logger, Level.INFO, eventType, message, *args)
    }

    /**
     * Log a debug message with structured context.
     */
    fun debug(logger: Logger, eventType: String, message: String, vararg args: Any) {
        log(logger, Level.DEBUG, eventType, message, *args)
    }

    /**
     * Log a trace message with structured context.
     */
    fun trace(logger: Logger, eventType: String, message: String, vararg args: Any) {
        log(logger, Level.TRACE, eventType, message, *args)
    }

    /**
     * Execute the given block with the specified context values,
     * then restore the previous context.
     */
    fun <T> withContext(
        sessionId: String? = null,
        userId: String? = null,
        gameId: Long? = null,
        requestId: String? = null,
        eventType: String? = null,
        block: () -> T
    ): T {
        val previousSessionId = MDC.get(SESSION_ID)
        val previousUserId = MDC.get(USER_ID)
        val previousGameId = MDC.get(GAME_ID)
        val previousRequestId = MDC.get(REQUEST_ID)
        val previousEventType = MDC.get(EVENT_TYPE)

        try {
            sessionId?.let { MDC.put(SESSION_ID, it) }
            userId?.let { MDC.put(USER_ID, it) }
            gameId?.let { MDC.put(GAME_ID, it.toString()) }
            requestId?.let { MDC.put(REQUEST_ID, it) }
            eventType?.let { MDC.put(EVENT_TYPE, it) }

            return block()
        } finally {
            // Restore previous context
            if (previousSessionId != null) MDC.put(SESSION_ID, previousSessionId) else MDC.remove(SESSION_ID)
            if (previousUserId != null) MDC.put(USER_ID, previousUserId) else MDC.remove(USER_ID)
            if (previousGameId != null) MDC.put(GAME_ID, previousGameId) else MDC.remove(GAME_ID)
            if (previousRequestId != null) MDC.put(REQUEST_ID, previousRequestId) else MDC.remove(REQUEST_ID)
            if (previousEventType != null) MDC.put(EVENT_TYPE, previousEventType) else MDC.remove(EVENT_TYPE)
        }
    }
}
