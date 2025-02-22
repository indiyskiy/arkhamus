package com.arkhamusserver.arkhamus.config.auth.logic

import com.arkhamusserver.arkhamus.config.auth.ArkhamusWebAuthenticationDetails
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.CustomUserDetailsService
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.TokenService
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class MainAuthLogic(
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val userAccountRepository: UserAccountRepository,
) {
    fun processToken(
        jwtToken: String,
        request: HttpServletRequest,
        filterChain: FilterChain,
        response: HttpServletResponse
    ) {
        val email = tokenService.extractEmail(jwtToken)
        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            val player = userAccountRepository.findByEmail(email)
            val foundUser = userDetailsService.mapToUserDetails(player)
            if (tokenService.isValid(jwtToken, foundUser)) updateContext(player.get(), foundUser, request)
            filterChain.doFilter(request, response)
        }
    }

    private fun updateContext(
        player: UserAccount,
        foundUser: UserDetails,
        request: HttpServletRequest
    ) {
        val authToken = UsernamePasswordAuthenticationToken(
            foundUser,
            null,
            foundUser.authorities
        )
        authToken.details = ArkhamusWebAuthenticationDetails(
            userAccount = player,
            context = request
        )
        SecurityContextHolder.getContext().authentication = authToken
    }
}