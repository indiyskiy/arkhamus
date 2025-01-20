package com.arkhamusserver.arkhamus.logic.auth

import com.arkhamusserver.arkhamus.logic.steam.SteamReaderLogic
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
    private val roleRepository: RoleRepository,
    private val encoder: PasswordEncoder,
    private val steamReaderLogic: SteamReaderLogic
) {

    companion object {
        private val logger = LoggerFactory.getLogger(SteamAuthService::class.java)
    }

    @Transactional
    fun authenticateSteam(steamId: String): AuthenticationResponse {
        logger.info("Authenticating Steam user with SteamID: {}", steamId)

        try {
            val userBySteamId = userAccountRepository.findBySteamId(steamId)
            if (userBySteamId.isPresent) {
                val existingUser = userBySteamId.get()
                logger.info("User found for SteamID {}: {}", steamId, existingUser)
                return authenticationService.authUser(
                    ArkhamusUserDetails(
                        existingUser.email ?: "",
                        existingUser.password ?: "",
                        existingUser.role,
                        existingUser
                    )
                )
            } else {
                logger.info("No user found for SteamID: {}, creating a new user.", steamId)
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
        } catch (e: Exception) {
            logger.error("Error during Steam authentication for SteamID: {}: {}", steamId, e.message)
            throw RuntimeException("Steam authentication failed for SteamID: $steamId", e)
        }
    }

    private fun createNewUser(steamId: String): UserAccount {
        logger.info("Fetching Steam user data for SteamID: {}", steamId)

        val steamUserData = steamReaderLogic.readSteamUserData(steamId)
        val userAccount = buildUser(steamUserData)

        logger.info("Saving new user to the database: {}", userAccount)
        return userAccountRepository.save(userAccount)
    }

    private fun buildUser(response: SteamUserResponse): UserAccount {
        logger.info("Building UserAccount from Steam user data: {}", response)

        val player = response.response?.players?.firstOrNull()
            ?: throw IllegalArgumentException("Invalid user data received from Steam.")

        return UserAccount(
            id = null,
            steamId = player.steamId,
            creationTimestamp = null,
            nickName = player.personaName ?: "Unknown",
            email = player.personaName, // You might need a separate property for email if Steam API provides one
            password = encoder.encode(generateRandomPassword()),
            role = setOf(roleRepository.findByName("ROLE_${RoleName.USER.name}").orElseThrow {
                IllegalStateException("Default user role not found in the database.")
            })
        )
    }

    private fun generateRandomPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*()-_=+{}[]|:;<>,.?/"
        return (1..12)
            .map { chars.random() }
            .joinToString("")
    }
}
