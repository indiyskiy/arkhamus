package com.arkhamusserver.arkhamus.logic.auth

import com.arkhamusserver.arkhamus.logic.steam.SteamReaderLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.RoleRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.ArkhamusUserDetails
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserSkinSettings
import com.arkhamusserver.arkhamus.model.enums.RoleName
import com.arkhamusserver.arkhamus.model.enums.SkinColor
import com.arkhamusserver.arkhamus.view.dto.steam.SteamUserResponse
import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationResponse
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
class SteamAuthService(
    private val authenticationService: AuthenticationService,
    private val userAccountRepository: UserAccountRepository,
    private val roleRepository: RoleRepository,
    private val encoder: PasswordEncoder,
    private val steamReaderLogic: SteamReaderLogic,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(SteamAuthService::class.java)
        private val random = Random(System.currentTimeMillis())
    }

    @Transactional
    fun authenticateSteam(steamId: String): AuthenticationResponse {
        logger.info("Authenticating Steam user with SteamID: {}", steamId)
        try {
            synchronized(steamId.intern()) {
                val userBySteamId = userAccountRepository.findBySteamId(steamId)
                val user = if (userBySteamId.isPresent) {
                    val existingUser = userBySteamId.get()
                    logger.info("User found for SteamID {}: {}", steamId, existingUser)
                    existingUser
                } else {
                    logger.info("No user found for SteamID: {}, creating a new user.", steamId)
                    createNewUser(steamId)
                }

                val auth = authenticationService.authUser(
                    ArkhamusUserDetails(
                        user.email ?: "",
                        user.password ?: "",
                        user.role,
                        user
                    )
                )
                return auth
            }
        } catch (e: Exception) {
            logger.error("Error during Steam authentication for SteamID: {}: {}", steamId, e.message)
            throw RuntimeException("Steam authentication failed for SteamID: $steamId", e)
        }
    }


    private fun createNewUser(steamId: String): UserAccount {
        logger.info("Fetching Steam user data for SteamID: {}", steamId)
        // Fetch and build the new UserAccount object from Steam data
        val steamUserData = steamReaderLogic.readSteamUserData(steamId)
        val userAccount = buildUser(steamUserData)
        logger.info("Saving new user to the database: {}", userAccount)
        // Create a UserSkinSettings object linked to the saved UserAccount
        val skin = generateSkin(userAccount)
        userAccount.userSkinSettings = skin
        val accountSaved = userAccountRepository.save(userAccount)
        logger.info("Saved new user to the database: {}", accountSaved)
        return accountSaved
    }


    private fun buildUser(response: SteamUserResponse): UserAccount {
        logger.info("Building UserAccount from Steam user data: {}", response)

        val player = response.response?.players?.firstOrNull()
            ?: throw IllegalArgumentException("Invalid user data received from Steam.")
        logger.info("Creating role set")
        val role = roleRepository.findByName(RoleName.USER.securityValue).orElseThrow {
            IllegalStateException("Default user role not found in the database.")
        }
        val roleSet = setOf(role)
        logger.info("Creating password")
        val password = encoder.encode(generateRandomPassword())

        return UserAccount(
            id = null,
            steamId = player.steamid,
            creationTimestamp = null,
            nickName = player.personaname,
            email = player.personaname,
            password = password,
            role = roleSet
        )
    }

    private fun generateSkin(userAccount: UserAccount): UserSkinSettings {
        return UserSkinSettings(
            id = null,
            skinColor = randomSkinColor(),
            userAccount = userAccount
        )
    }


    private fun randomSkinColor(): SkinColor {
        return SkinColor.values().random(random)
    }

    private fun generateRandomPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*()-_=+{}[]|:;<>,.?/"
        return (1..12)
            .map { chars.random() }
            .joinToString("")
    }
}
