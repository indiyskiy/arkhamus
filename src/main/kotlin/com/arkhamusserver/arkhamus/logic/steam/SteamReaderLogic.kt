package com.arkhamusserver.arkhamus.logic.steam

import com.arkhamusserver.arkhamus.logic.CurrentUserService
import com.arkhamusserver.arkhamus.logic.steam.SteamHandler.Companion.VERY_SECRET_API_KEY
import com.arkhamusserver.arkhamus.model.UserStateHolder
import com.arkhamusserver.arkhamus.model.dataaccess.UserStatusService
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.steam.SteamPersonaState
import com.arkhamusserver.arkhamus.view.dto.steam.FriendListResponse
import com.arkhamusserver.arkhamus.view.dto.steam.PlayerData
import com.arkhamusserver.arkhamus.view.dto.steam.SteamUserResponse
import com.arkhamusserver.arkhamus.view.dto.user.SteamUserShortDto
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class SteamReaderLogic(
    private val steamHandler: SteamHandler,
    private val currentUserService: CurrentUserService,
    private val userAccountRepository: UserAccountRepository,
    private val userStatusService: UserStatusService,
    private val webClient: WebClient.Builder
) {

    companion object {
        private val logger = LoggerFactory.getLogger(SteamReaderLogic::class.java)
        //private val FRIENDS_URL = "https://api.steampowered.com/ISteamUserISteamUser/GetFriendList/v1"
        private val FRIENDS_URL = "https://partner.steam-api.com/ISteamUser/GetFriendList/v1/"
    }

    fun readSteamUserData(steamId: String): SteamUserResponse {
        logger.info("Fetching Steam user data for SteamID: {}", steamId)
        try {
            // Call SteamHandler or another external API library to fetch user data
            val response = steamHandler.fetchUserData(steamId)

            if (response != null) {
                logger.info("Successfully fetched user data for SteamID: {}", steamId)
                return response // Assume this maps to SteamUserResponse
            } else {
                logger.warn("No user data found for SteamID: {}", steamId)
                // Handle empty or invalid responses
                throw IllegalStateException("Steam user data not found for SteamID: $steamId")
            }
        } catch (e: Exception) {
            logger.error("Error fetching Steam user data for SteamID: {}: {}", steamId, e.message)
            throw RuntimeException("Unable to fetch Steam user data", e)
        }
    }

    fun readFriendList(): List<SteamUserShortDto> {
        val steamId = currentUserService.getCurrentUserAccount().steamId

        val response = try {
            queueFriends(steamId)
        } catch (e: Exception) {
            logger.warn("Failed to fetch friend list from steam: {}", e.message)
            FriendListResponse(friendslist = null)
        }
        val friends = response.friendslist?.friends ?: emptyList()
        return readSteamUsers(friends.map { it.steamid })
    }

    fun readFriendList(steamIds: String): List<SteamUserShortDto> {
        val steamIdsList = steamIds.split(",").map { it.trim() }
        return readSteamUsers(steamIdsList)
    }

    private fun readSteamUsers(steamIdsList: List<String>): List<SteamUserShortDto> {
        val realUsers = steamIdsList.mapNotNull {
            userAccountRepository.findBySteamId(it).orElse(null)
        }.associateBy {
            it.steamId
        }
        val steamUserFriends = steamIdsList.mapNotNull {
            readSteamUserData(it).response?.players?.firstOrNull()
        }.associateBy {
            it.steamid
        }
        val states = realUsers.mapNotNull {
            it.value.id
        }.map {
            userStatusService.getUserStatus(it)
        }.associateBy {
            it.userId
        }
        return steamIdsList.map {
            val steamUser = steamUserFriends[it]
            val user = realUsers[it]
            val state = user?.let { states[user.id] }
            it.toDto(user, steamUser, state)
        }
    }

    private fun queueFriends(steamId: String?): FriendListResponse {
        val friendListResponse = webClient.build()
            .get()
            .uri(FRIENDS_URL) {
                it.queryParam("key", VERY_SECRET_API_KEY)
                    .queryParam("steamid", steamId)
                    .queryParam("relationship", "friend")
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
        val gson = Gson()
        val response = gson.fromJson(friendListResponse, FriendListResponse::class.java)
        return response
    }

    private fun String.toDto(
        userAccount: UserAccount?,
        steamPlayer: PlayerData?,
        state: UserStateHolder?
    ) = this.let { steamId ->
        SteamUserShortDto().apply {
            this.steamId = steamId
            this.steamState = steamPlayer?.personastate?.let { SteamPersonaState.fromId(it) }
            this.nickName = userAccount?.nickName ?: steamPlayer?.personaname
            this.userId = userAccount?.id
            this.cultpritsState = state?.userState
            this.lastActive = state?.lastActive
        }
    }
}




