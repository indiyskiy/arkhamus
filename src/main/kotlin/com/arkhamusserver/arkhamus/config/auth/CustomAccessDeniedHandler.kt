package com.arkhamusserver.arkhamus.config.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import java.io.IOException

class CustomAccessDeniedHandler : AccessDeniedHandler {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(CustomAccessDeniedHandler::class.java)
    }


    @Throws(IOException::class)
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        logger.error("Access denied: " + accessDeniedException.message, accessDeniedException)
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: " + accessDeniedException.message)
    }
}