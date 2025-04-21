package com.arkhamusserver.arkhamus.view.controller.steam

import com.arkhamusserver.arkhamus.logic.user.steam.SteamAuthLogic
import com.arkhamusserver.arkhamus.logic.user.steam.SteamStartServerLogic
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.steam.SteamAuthRequestDto
import com.arkhamusserver.arkhamus.view.dto.steam.SteamServerIdDto
import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public/steam")
class SteamAuthController(
    private val steamAuthLogic: SteamAuthLogic,
    private val steamStartServerLogic: SteamStartServerLogic,
) {
    companion object {
        private val logger = LoggingUtils.getLogger<SteamAuthController>()
    }

    // Retrieve the Steam game server's SteamID
    @GetMapping("/server-steamid")
    fun getServerSteamID(): SteamServerIdDto {
        try {
            val serverSteamID = steamStartServerLogic.getServerSteamID()
            return serverSteamID ?: throw Exception("Failed to retrieve Server SteamID.")
        } catch (e: Exception) {
            LoggingUtils.withContext(
                eventType = LoggingUtils.EVENT_STEAM
            ) {
                logger.error("Failed to retrieve Server SteamID: {}", e.message)
            }
            throw e
        }
    }

    // Authenticate a client by SteamID and auth ticket
    @PostMapping("/authenticate-client")
    fun authenticateClient(
        @RequestBody steamAuthRequestDto: SteamAuthRequestDto
    ): AuthenticationResponse {
        return steamAuthLogic.authenticateClient(
            steamAuthRequestDto.clientSteamID,
            steamAuthRequestDto.authTicket
        )
    }

    // Handle client disconnection
    @PostMapping("/disconnect-client")
    fun handleClientDisconnect(@RequestParam clientSteamID: String): ResponseEntity<String> {
        return try {
            steamAuthLogic.handleClientDisconnect(clientSteamID)
            ResponseEntity.ok("Client disconnected and session ended. SteamID: $clientSteamID")
        } catch (e: Exception) {
            LoggingUtils.withContext(
                eventType = LoggingUtils.EVENT_STEAM
            ) {
                logger.error("Error while disconnecting client. SteamID: {}, Error: {}", clientSteamID, e.message)
            }
            ResponseEntity.internalServerError().body("Error disconnecting client: ${e.message}")
        }
    }

}