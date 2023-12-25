package com.arkhamusserver.arkhamus.model.dataaccess.auth

import com.arkhamusserver.arkhamus.model.dataaccess.UserAccountRepository
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
            UsernameNotFoundException("Not found!")
        }
            ?.mapToUserDetailsExt()
            ?: throw UsernameNotFoundException("Not found!")

    fun UserAccount.mapToUserDetailsExt(): UserDetails =
        User.builder()
            .username(this.email)
            .password(this.password)
            .roles(this.role?.name)
            .build()

    fun mapToUserDetails(user: Optional<UserAccount>): UserDetails =
        user.orElseThrow { UsernameNotFoundException("Not found!") }.mapToUserDetailsExt()
}

