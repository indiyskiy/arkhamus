package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class RefreshTokenRepository {
    private val tokens = mutableMapOf<String, UserDetails>()
    fun save(token: String, userDetails: UserDetails) {
        tokens[token] = userDetails
    }
}