package com.arkhamusserver.arkhamus.utils

import com.arkhamusserver.arkhamus.config.auth.ArkhamusWebAuthenticationDetails
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import jakarta.servlet.http.HttpServletRequest
import org.mockito.Mockito
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component

@Component
class FakeUserSetupUtil(
    private val userAccountRepository: UserAccountRepository
) {
    fun fakeUser(userEmail: String): UserAccount {
        val user = userAccountRepository.findByEmail(userEmail).get()
        return fakeUser(user)
    }

    private fun fakeUser(fakeUser: UserAccount): UserAccount {
        // create a mock user
        val fakeUserAccount = User(
            fakeUser.nickName,
            fakeUser.password,
            AuthorityUtils.createAuthorityList(fakeUser.role.first().name)
        )
        val request: HttpServletRequest = Mockito.mock(HttpServletRequest::class.java)
        Mockito.`when`(request.remoteAddr).thenReturn("127.0.0.1")
        val authToken = UsernamePasswordAuthenticationToken(fakeUserAccount, null, fakeUserAccount.authorities)
        authToken.details = ArkhamusWebAuthenticationDetails(fakeUser, request)
        SecurityContextHolder.getContext().authentication = authToken
        return fakeUser
    }
}
