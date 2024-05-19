package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth

import com.arkhamusserver.arkhamus.model.database.entity.Role
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class ArkhamusUserDetails(
    email: String,
    password: String,
    roles: Set<Role>,
    val userAccount: UserAccount
) : User(
    email,
    password,
    mutableListOf(SimpleGrantedAuthority(roles.first().name))
)