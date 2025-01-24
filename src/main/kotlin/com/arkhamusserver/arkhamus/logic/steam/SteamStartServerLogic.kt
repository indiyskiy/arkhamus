package com.arkhamusserver.arkhamus.logic.steam

import com.arkhamusserver.arkhamus.view.dto.steam.SteamServerIdDto
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SteamStartServerLogic(
    private val steamHandler: SteamHandler,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SteamStartServerLogic::class.java)
    }

    // Automatically initialize the game server on application startup
    @PostConstruct
    fun initializeSteamServer() {
        logger.info("Initializing Steam server automatically on application startup.")
        startServer()
    }

    // Initialize the Steam game server
    fun startServer() {
        try {
            steamHandler.initSteamServer()
        } catch (e: Exception) {
            logger.error("Failed to start Steam server: {}", e.message)
            throw e
        }
    }

    fun getServerSteamID(): SteamServerIdDto? {
        return steamHandler.getServerSteamID()
    }
}