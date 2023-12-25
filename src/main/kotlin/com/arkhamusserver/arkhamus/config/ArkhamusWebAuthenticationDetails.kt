package com.arkhamusserver.arkhamus.config

import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetails

class ArkhamusWebAuthenticationDetails(
    val userAccount: UserAccount,
    context: HttpServletRequest
) : WebAuthenticationDetails(context)