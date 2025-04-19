package com.arkhamusserver.arkhamus.logic.user.steam

import com.arkhamusserver.arkhamus.logic.auth.SteamAuthService
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationResponse
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SteamAuthLogic(
    private val steamHandler: SteamHandler,
    private val steamAuthService: SteamAuthService
) {
    companion object {
        private val logger = LoggingUtils.getLogger<SteamAuthLogic>()
    }

   @Transactional
    // Authenticate a connecting client
    fun authenticateClient(
        clientSteamID: String,
        authTicket: String
    ): AuthenticationResponse {
        try {
            // Authenticate the client using the SteamHandler
            val isAuthenticated = steamHandler.authenticateClientTicket(
                clientSteamID,
                authTicket.parseToByteArray()
            )
            if (isAuthenticated) {
                logger.info("Client authenticated successfully. SteamID: {}", clientSteamID)
                return steamAuthService.authenticateSteam(clientSteamID)
            } else {
                logger.warn("Client authentication failed. SteamID: {}", clientSteamID)
                throw RuntimeException("Client authentication failed for SteamID: $clientSteamID")
            }
        } catch (e: Exception) {
            logger.error("Error during client authentication. SteamID: {}, Error: {}", clientSteamID, e.message)
            throw e
        }
    }

    // Handle the disconnection of a client
    fun handleClientDisconnect(clientSteamID: String) {
        try {
            // Terminate the client's authentication session
            steamHandler.endClientAuthSession(clientSteamID)
            logger.info("Client disconnected. Authentication session ended for SteamID: {}", clientSteamID)
        } catch (e: Exception) {
            logger.error("Error while handling client disconnect. SteamID: {}, Error: {}", clientSteamID, e.message)
        }
    }

    // Shut down the Steam game server
    fun shutdownServer() {
        try {
            // Shutdown the SteamHandler / server
            steamHandler.shutdownServer()
            logger.info("Steam game server shut down successfully.")
        } catch (e: Exception) {
            logger.error("Error during Steam server shutdown: {}", e.message)
        }
    }

    private fun String.parseToByteArray(): ByteArray {
        val numbers = this.removeSurrounding("[", "]").split(",").map { it.trim().toInt() }
        return numbers.map { it.toByte() }.toByteArray()

    }
}
