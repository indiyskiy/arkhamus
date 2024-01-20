package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomUserDetailsService(
    private val userRepository: UserAccountRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByEmail(username).orElseThrow {
            UsernameNotFoundException("User not found!")
        }
            ?.mapToUserDetailsExt()
            ?: throw UsernameNotFoundException("User not found!")

    fun UserAccount.mapToUserDetailsExt(): UserDetails =
        User.builder()
            .username(this.email)
            .password(this.password)
            .roles(this.role?.name)
            .build()

    fun mapToUserDetails(user: Optional<UserAccount>): UserDetails =
        user.orElseThrow { UsernameNotFoundException("User not found!") }.mapToUserDetailsExt()
}

