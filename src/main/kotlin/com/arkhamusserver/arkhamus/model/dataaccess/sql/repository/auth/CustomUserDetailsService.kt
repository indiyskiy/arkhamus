package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomUserDetailsService(
    private val userRepository: UserAccountRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): ArkhamusUserDetails =
        userRepository.findByEmail(username).orElseThrow {
            UsernameNotFoundException("User not found!")
        }
            ?.mapToUserDetailsExt()
            ?: throw UsernameNotFoundException("User not found!")

    fun UserAccount.mapToUserDetailsExt(): ArkhamusUserDetails =
        ArkhamusUserDetails(
            email!!,
            password!!,
            role.name,
            this
        )

    fun mapToUserDetails(user: Optional<UserAccount>): UserDetails =
        user.orElseThrow { UsernameNotFoundException("User not found!") }.mapToUserDetailsExt()
}

