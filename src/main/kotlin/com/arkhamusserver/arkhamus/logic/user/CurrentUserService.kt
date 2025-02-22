package com.arkhamusserver.arkhamus.logic.user

import com.arkhamusserver.arkhamus.config.auth.ArkhamusWebAuthenticationDetails
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class CurrentUserService {
    fun getCurrentUserAccount(): UserAccount {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        val details = auth.details as ArkhamusWebAuthenticationDetails
        return details.userAccount
    }
}
