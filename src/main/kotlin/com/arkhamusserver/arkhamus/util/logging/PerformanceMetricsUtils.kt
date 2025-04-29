package com.arkhamusserver.arkhamus.util.logging

import org.slf4j.Logger
import java.util.concurrent.TimeUnit

/**
 * Utility class for measuring and logging performance metrics of critical operations.
 * This class provides methods to measure execution time and log it using the structured logging approach.
 */
object PerformanceMetricsUtils {

    /**
     * Measures the execution time of the provided block and logs it as a performance metric.
     * 
     * @param logger The logger to use for logging
     * @param operationName The name of the operation being measured
     * @param additionalContext Additional context information to include in the log (optional)
     * @param block The code block to measure
     * @return The result of the block execution
     */
    inline fun <T> measureAndLog(
        logger: Logger,
        operationName: String,
        additionalContext: Map<String, String> = emptyMap(),
        block: () -> T
    ): T {
        val startTime = System.nanoTime()
        try {
            return block()
        } finally {
            val endTime = System.nanoTime()
            val executionTimeMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)

            // Build the log message with additional context
            val contextInfo = if (additionalContext.isNotEmpty()) {
                additionalContext.entries.joinToString(", ") { "${it.key}=${it.value}" }
            } else {
                ""
            }

            val message = if (contextInfo.isNotEmpty()) {
                "Operation '$operationName' completed in $executionTimeMs ms [$contextInfo]"
            } else {
                "Operation '$operationName' completed in $executionTimeMs ms"
            }

            LoggingUtils.info(logger, LoggingUtils.EVENT_PERFORMANCE, message)
        }
    }

    /**
     * Measures the execution time of a sub-operation and logs it as a performance metric.
     * 
     * @param logger The logger to use for logging
     * @param subOperationName The name of the sub-operation being measured
     * @param additionalContext Additional context information to include in the log (optional)
     * @param block The code block to measure
     * @return The result of the block execution
     */
    inline fun <T> measureSubOperation(
        logger: Logger,
        subOperationName: String,
        additionalContext: Map<String, String> = emptyMap(),
        block: () -> T
    ): T {
        val startTime = System.nanoTime()
        try {
            return block()
        } finally {
            val endTime = System.nanoTime()
            val executionTimeMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)

            // Build the log message with additional context
            val contextInfo = if (additionalContext.isNotEmpty()) {
                additionalContext.entries.joinToString(", ") { "${it.key}=${it.value}" }
            } else {
                ""
            }

            val message = if (contextInfo.isNotEmpty()) {
                "Sub-operation '$subOperationName' completed in $executionTimeMs ms [$contextInfo]"
            } else {
                "Sub-operation '$subOperationName' completed in $executionTimeMs ms"
            }

            LoggingUtils.info(logger, LoggingUtils.EVENT_PERFORMANCE, message)
        }
    }

    /**
     * Measures the execution time of multiple operations within a parent operation and logs them as performance metrics.
     * This is useful for measuring the performance of sub-operations within a larger operation.
     * 
     * @param logger The logger to use for logging
     * @param parentOperationName The name of the parent operation
     * @param additionalContext Additional context information to include in the log (optional)
     * @param block The code block that will execute operations
     * @return The result of the block execution
     */
    inline fun <T> measureParentOperation(
        logger: Logger,
        parentOperationName: String,
        additionalContext: Map<String, String> = emptyMap(),
        block: () -> T
    ): T {
        val startTime = System.nanoTime()

        try {
            return block()
        } finally {
            val endTime = System.nanoTime()
            val executionTimeMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime)

            // Build the log message with additional context
            val contextInfo = if (additionalContext.isNotEmpty()) {
                additionalContext.entries.joinToString(", ") { "${it.key}=${it.value}" }
            } else {
                ""
            }

            val message = if (contextInfo.isNotEmpty()) {
                "Parent operation '$parentOperationName' completed in $executionTimeMs ms [$contextInfo]"
            } else {
                "Parent operation '$parentOperationName' completed in $executionTimeMs ms"
            }

            LoggingUtils.info(logger, LoggingUtils.EVENT_PERFORMANCE, message)
        }
    }
}
