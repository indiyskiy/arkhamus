package com.arkhamusserver.arkhamus.config.auth

import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.web.authentication.WebAuthenticationDetails

class ArkhamusWebAuthenticationDetails(
    val userAccount: UserAccount,
    context: HttpServletRequest?
) : WebAuthenticationDetails(context)