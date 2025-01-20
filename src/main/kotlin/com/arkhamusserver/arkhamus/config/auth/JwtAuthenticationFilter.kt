package com.arkhamusserver.arkhamus.config.auth

import com.arkhamusserver.arkhamus.config.auth.logic.AdminAuthLogic
import com.arkhamusserver.arkhamus.config.auth.logic.BearerAuthLogic
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val bearerAuthLogic: BearerAuthLogic,
    private val adminAuthLogic: AdminAuthLogic,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        try {
            val tokenCookie = adminAuthLogic.getAdminAuthData(request)
            if (tokenCookie != null) {
                val jwtToken: String? = tokenCookie.value
                if (!jwtToken.isNullOrEmpty()) {
                    adminAuthLogic.processAdminAuth(jwtToken, request, filterChain, response)
                } else {
                    bearerAuthLogic.tryBearer(request, filterChain, response)
                }
            } else {
                bearerAuthLogic.tryBearer(request, filterChain, response)
            }
        } catch (e: Exception) {
            logger.error(e)
            throw e
        }
    }
}