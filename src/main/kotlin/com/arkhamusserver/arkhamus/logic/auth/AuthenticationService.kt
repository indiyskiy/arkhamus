package com.arkhamusserver.arkhamus.logic.auth

import com.arkhamusserver.arkhamus.config.CultpritsUserState
import com.arkhamusserver.arkhamus.config.auth.JwtProperties
import com.arkhamusserver.arkhamus.model.dataaccess.UserStatusService
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.ArkhamusUserDetails
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.CustomUserDetailsService
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.RefreshTokenRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.TokenService
import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationRequest
import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationResponse
import com.arkhamusserver.arkhamus.view.dto.user.UserDto
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userStatusService: UserStatusService,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationService::class.java)
    }

    @Transactional
    fun authentication(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.login,
                authenticationRequest.password
            )
        )
        val user = userDetailsService.loadUserByUsername(authenticationRequest.login)
        userStatusService.updateUserStatus(user.userAccount.id!!, CultpritsUserState.ONLINE, true)
        return authUser(user)
    }

    fun authUser(user: ArkhamusUserDetails): AuthenticationResponse {
        logger.info("Authenticating user: {}", user.username)
        val accessToken = createAccessToken(user)
        val refreshToken = createRefreshToken(user)
        logger.info("Authenticating user: {}", user.username)
        refreshTokenRepository.save(refreshToken, user)
        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            UserDto(
                user.userAccount.id!!,
                user.userAccount.steamId,
                user.userAccount.nickName
            )
        )
    }

    private fun createAccessToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = getAccessTokenExpiration()
    )

    private fun createRefreshToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = getRefreshTokenExpiration()
    )

    private fun getAccessTokenExpiration(): Date =
        Date(System.currentTimeMillis() + jwtProperties.access)

    private fun getRefreshTokenExpiration(): Date =
        Date(System.currentTimeMillis() + jwtProperties.refresh)
}