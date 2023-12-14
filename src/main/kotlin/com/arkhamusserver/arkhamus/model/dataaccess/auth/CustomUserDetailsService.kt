package com.arkhamusserver.arkhamus.model.dataaccess.auth

import com.arkhamusserver.arkhamus.model.dataaccess.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserAccountRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByEmail(username).orElseThrow {
            UsernameNotFoundException("Not found!")
        }
            ?.mapToUserDetails()
            ?: throw UsernameNotFoundException("Not found!")

    private fun UserAccount.mapToUserDetails(): UserDetails =
        User.builder()
            .username(this.email)
            .password(this.password)
            .roles(this.role?.name)
            .build()
}