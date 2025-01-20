package com.arkhamusserver.arkhamus.logic.steam

import com.arkhamusserver.arkhamus.view.dto.steam.SteamUserResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SteamReaderLogic(
   private val steamHandler: SteamHandler
) {

    companion object {
        private val logger = LoggerFactory.getLogger(SteamReaderLogic::class.java)
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

}