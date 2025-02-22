package com.arkhamusserver.arkhamus.view.controller.steam

import com.arkhamusserver.arkhamus.logic.user.steam.SteamAuthLogic
import com.arkhamusserver.arkhamus.logic.user.steam.SteamStartServerLogic
import com.arkhamusserver.arkhamus.view.dto.steam.SteamAuthRequestDto
import com.arkhamusserver.arkhamus.view.dto.steam.SteamServerIdDto
import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public/steam")
class SteamAuthController(
    private val steamAuthLogic: SteamAuthLogic,
    private val steamStartServerLogic: SteamStartServerLogic,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SteamAuthController::class.java)
    }

    // Retrieve the Steam game server's SteamID
    @GetMapping("/server-steamid")
    fun getServerSteamID(): SteamServerIdDto {
        try {
            logger.info("Received request to get server SteamID.")
            val serverSteamID = steamStartServerLogic.getServerSteamID()
            return serverSteamID ?: throw Exception("Failed to retrieve Server SteamID.")
        } catch (e: Exception) {
            logger.error("Failed to retrieve Server SteamID: {}", e.message)
            throw e
        }
    }

    // Authenticate a client by SteamID and auth ticket
    @PostMapping("/authenticate-client")
    fun authenticateClient(
        @RequestBody steamAuthRequestDto: SteamAuthRequestDto
    ): AuthenticationResponse {
        logger.info("Received request to authenticate client with SteamID: {}", steamAuthRequestDto.clientSteamID)
        return steamAuthLogic.authenticateClient(
            steamAuthRequestDto.clientSteamID,
            steamAuthRequestDto.authTicket
        )
    }

    // Handle client disconnection
    @PostMapping("/disconnect-client")
    fun handleClientDisconnect(@RequestParam clientSteamID: String): ResponseEntity<String> {
        return try {
            logger.info("Received request to disconnect client with SteamID: {}", clientSteamID)
            steamAuthLogic.handleClientDisconnect(clientSteamID)
            ResponseEntity.ok("Client disconnected and session ended. SteamID: $clientSteamID")
        } catch (e: Exception) {
            logger.error("Error while disconnecting client. SteamID: {}, Error: {}", clientSteamID, e.message)
            ResponseEntity.internalServerError().body("Error disconnecting client: ${e.message}")
        }
    }

}