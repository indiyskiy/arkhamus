package com.arkhamusserver.arkhamus.logic.user.steam

import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.steam.SteamServerIdDto
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class SteamStartServerLogic(
    private val steamHandler: SteamHandler,
) {
    companion object {
        private val logger = LoggingUtils.getLogger<SteamStartServerLogic>()
    }

    // Automatically initialize the game server on application startup
    @PostConstruct
    fun initializeSteamServer() {
        LoggingUtils.withContext(
            eventType = LoggingUtils.EVENT_STEAM
        ) {
            logger.info("Initializing Steam server automatically on application startup.")
        }
        startServer()
    }

    // Initialize the Steam game server
    fun startServer() {
        try {
            steamHandler.initSteamServer()
        } catch (e: Exception) {
            LoggingUtils.withContext(
                eventType = LoggingUtils.EVENT_STEAM
            ) {
                logger.error("Failed to start Steam server: {}", e.message)
            }
            throw e
        }
    }

    fun getServerSteamID(): SteamServerIdDto? {
        return steamHandler.getServerSteamID()
    }
}