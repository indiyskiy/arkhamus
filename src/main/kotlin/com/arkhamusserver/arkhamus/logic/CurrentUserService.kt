package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.config.ArkhamusWebAuthenticationDetails
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
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
