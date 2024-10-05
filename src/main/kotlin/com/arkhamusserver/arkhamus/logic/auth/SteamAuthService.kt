package com.arkhamusserver.arkhamus.logic.auth

import com.arkhamusserver.arkhamus.logic.steam.SteamLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.RoleRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.ArkhamusUserDetails
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.RoleName
import com.arkhamusserver.arkhamus.view.dto.steam.SteamUserResponse
import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationResponse
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SteamAuthService(
    private val authenticationService: AuthenticationService,
    private val userAccountRepository: UserAccountRepository,
    private val steamLogic: SteamLogic,
    private val roleRepository: RoleRepository,
    private val encoder: PasswordEncoder
) {

    companion object {
        private val logger = LoggerFactory.getLogger(SteamAuthService::class.java)
    }

    @Transactional
    fun authenticationSteam(steamId: String): AuthenticationResponse {
        val userBySteamId = userAccountRepository.findBySteamId(steamId)
        if (userBySteamId.isPresent) {
            with(userBySteamId.get()) {
                return authenticationService.authUser(
                    ArkhamusUserDetails(
                        this.email ?: "",
                        this.password ?: "",
                        this.role,
                        this
                    )
                )
            }
        } else {
            val newUser = createNewUser(steamId)
            return authenticationService.authUser(
                ArkhamusUserDetails(
                    newUser.email ?: "",
                    newUser.password ?: "",
                    newUser.role,
                    newUser
                )
            )
        }
    }

    private fun createNewUser(steamId: String): UserAccount {
        val steamUserData = steamLogic.readStaemUserData(steamId)
        val user = buildUser(steamUserData)
        return userAccountRepository.save(user)
    }

    private fun buildUser(response: SteamUserResponse.SteamUserResponse): UserAccount {
        val player = response.response!!.players.first()
        logger.info("player: $player")
        return UserAccount(
            id = null,
            steamId = player.steamid,
            creationTimestamp = null,
            nickName = player.personaname!!,
            email = player.personaname,
            password = encoder.encode(generateRandomPassword()),
            role = setOf(roleRepository.findByName("ROLE_${RoleName.USER.name}").get()),
        )
    }

    private fun generateRandomPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*()-_=+{}[]|:;<>,.?/"
        val password = StringBuilder()

        repeat(12) {
            password.append(chars.random())
        }

        return password.toString()
    }
}