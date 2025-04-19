package com.arkhamusserver.arkhamus.config.auth.logic

import com.arkhamusserver.arkhamus.config.auth.CustomAccessDeniedHandler
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdminAuthLogic(
    private val mainAuthLogic: MainAuthLogic,
) {
    companion object {
        private val logger = LoggingUtils.getLogger<AdminAuthLogic>()
    }

    fun getAdminAuthData(request: HttpServletRequest): Cookie? {
        val cookies: Array<Cookie>? = request.cookies
        val tokenCookie = cookies?.firstOrNull { cookie -> cookie.name.equals("token") }
        return tokenCookie
    }

    fun processAdminAuth(
        jwtToken: String,
        request: HttpServletRequest,
        filterChain: FilterChain,
        response: HttpServletResponse
    ) {
        try {
            mainAuthLogic.processToken(jwtToken, request, filterChain, response)
        } catch (e: ExpiredJwtException) {
            logger.warn("processAdminAuth failed", e)
            response.addCookie(
                Cookie("token", null).apply {
                    maxAge = 0
                    isHttpOnly = true
                    path = "/"
                }
            )
        }
    }
}