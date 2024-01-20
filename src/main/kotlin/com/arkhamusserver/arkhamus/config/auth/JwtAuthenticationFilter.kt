package com.arkhamusserver.arkhamus.config.auth

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.CustomUserDetailsService
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.TokenService
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val userAccountRepository: UserAccountRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")
        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
            return
        }
        val jwtToken = authHeader!!.extractTokenValue()
        val email = tokenService.extractEmail(jwtToken)
        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            val player = userAccountRepository.findByEmail(email)
            val foundUser = userDetailsService.mapToUserDetails(player)
            if (tokenService.isValid(jwtToken, foundUser))
                updateContext(player.get(), foundUser, request)
            filterChain.doFilter(request, response)
        }
    }

    private fun String?.doesNotContainBearerToken() =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() =
        this.substringAfter("Bearer ")

    private fun updateContext(player: UserAccount, foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = ArkhamusWebAuthenticationDetails(
            userAccount = player,
            context = request
        )
        SecurityContextHolder.getContext().authentication = authToken
    }
}