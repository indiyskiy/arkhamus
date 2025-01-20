package com.arkhamusserver.arkhamus.config.auth.logic

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component

@Component
class BearerAuthLogic(
    private val mainAuthLogic: MainAuthLogic,
) {
    fun tryBearer(
        request: HttpServletRequest, filterChain: FilterChain, response: HttpServletResponse
    ) {
        val authHeader: String? = request.getHeader("Authorization")
        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
        } else {
            val jwtToken = authHeader!!.extractTokenValue()
            mainAuthLogic.processToken(jwtToken, request, filterChain, response)
        }
    }

    private fun String?.doesNotContainBearerToken() = this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() = this.substringAfter("Bearer ")
}