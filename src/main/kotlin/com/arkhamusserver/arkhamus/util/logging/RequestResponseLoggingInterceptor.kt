package com.arkhamusserver.arkhamus.util.logging

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*

/**
 * Interceptor for logging API requests and responses.
 * This interceptor logs details about incoming requests and outgoing responses
 * to provide better visibility into API usage and performance.
 */
@Component
class RequestResponseLoggingInterceptor : HandlerInterceptor {

    private val logger: Logger = LoggingUtils.getLogger<RequestResponseLoggingInterceptor>()
    
    /**
     * Called before the handler is executed.
     * Logs information about the incoming request.
     */
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod) {
            // Generate a unique request ID for tracking this request through the logs
            val requestId = UUID.randomUUID().toString()
            request.setAttribute("requestId", requestId)
            
            // Set the start time for duration calculation
            request.setAttribute("requestStartTime", System.currentTimeMillis())
            
            // Log the request details
            LoggingUtils.withContext(
                requestId = requestId,
                eventType = LoggingUtils.EVENT_API
            ) {
                val method = request.method
                val uri = request.requestURI
                val queryString = request.queryString
                val fullUrl = if (queryString != null) "$uri?$queryString" else uri
                val contentType = request.contentType ?: "unknown"
                val userAgent = request.getHeader("User-Agent") ?: "unknown"
                
                LoggingUtils.info(
                    logger,
                    LoggingUtils.EVENT_API,
                    "Request: {} {} [Content-Type: {}, User-Agent: {}]",
                    method,
                    fullUrl,
                    contentType,
                    userAgent
                )
            }
        }
        return true
    }
    
    /**
     * Called after the handler is executed.
     * Logs information about the outgoing response.
     */
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        if (handler is HandlerMethod) {
            val requestId = request.getAttribute("requestId") as? String
            
            // Log the response details
            LoggingUtils.withContext(
                requestId = requestId,
                eventType = LoggingUtils.EVENT_API
            ) {
                val method = request.method
                val uri = request.requestURI
                val status = response.status
                val duration = calculateRequestDuration(request)
                
                if (ex != null) {
                    LoggingUtils.error(
                        logger,
                        LoggingUtils.EVENT_API,
                        "Response: {} {} - {} [{}ms] - Error: {}",
                        method,
                        uri,
                        status,
                        duration,
                        ex.message ?: "Unknown error"
                    )
                } else {
                    LoggingUtils.info(
                        logger,
                        LoggingUtils.EVENT_API,
                        "Response: {} {} - {} [{}ms]",
                        method,
                        uri,
                        status,
                        duration
                    )
                }
            }
        }
    }
    
    /**
     * Calculate the duration of the request processing.
     */
    private fun calculateRequestDuration(request: HttpServletRequest): Long {
        val startTime = request.getAttribute("requestStartTime") as? Long ?: System.currentTimeMillis()
        return System.currentTimeMillis() - startTime
    }
}