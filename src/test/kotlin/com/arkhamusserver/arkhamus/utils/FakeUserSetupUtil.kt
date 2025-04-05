package com.arkhamusserver.arkhamus.utils

import com.arkhamusserver.arkhamus.config.auth.ArkhamusWebAuthenticationDetails
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Role
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.user.UserSkinSettings
import com.arkhamusserver.arkhamus.model.enums.SkinColor
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.mockito.Mockito
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.util.*

@Component
class FakeUserSetupUtil() {

    private val userAccountRepository = mockk<UserAccountRepository>().also { it ->
        every { it.findByEmail(UserContainer.GRAF_D) } returns Optional.of<UserAccount>(fakeAccount(UserContainer.GRAF_D))
        every { it.findByEmail(UserContainer.INDIYSKIY) } returns Optional.of<UserAccount>(fakeAccount(UserContainer.INDIYSKIY))
        every { it.findByEmail(UserContainer.SITHOID) } returns Optional.of<UserAccount>(fakeAccount(UserContainer.SITHOID))
    }

    private fun fakeAccount(email: String): UserAccount {
      return UserAccount(nickName = email).apply {
          this.email = email
          this.steamId = email
          this.id = email.hashCode().toLong()
          this.password = email
          this.role = setOf(Role(0, name = "ADMIN"))
          this.userSkinSettings = UserSkinSettings(
              id = email.hashCode().toLong(),
              userAccount = this,
              skinColor = SkinColor.SKY
          )
          this.creationTimestamp = Timestamp(System.currentTimeMillis())
      }
    }


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
