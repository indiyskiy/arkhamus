package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class NettyAuthService(
    private val tokenService: TokenService,
    private val userAccountRepository: UserAccountRepository
) {
    fun auth(jwtToken: String): UserAccount? {
        val email = tokenService.extractEmail(jwtToken)
        if (email != null && SecurityContextHolder.getContext().authentication == null) {
            return userAccountRepository.findByEmail(email).orElse(null)
        }
        return null
    }

}