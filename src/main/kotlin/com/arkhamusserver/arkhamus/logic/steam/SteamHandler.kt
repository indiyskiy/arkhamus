package com.arkhamusserver.arkhamus.logic.steam

import com.arkhamusserver.arkhamus.view.dto.steam.PlayerData
import com.arkhamusserver.arkhamus.view.dto.steam.SteamResponseBody
import com.arkhamusserver.arkhamus.view.dto.steam.SteamServerIdDto
import com.arkhamusserver.arkhamus.view.dto.steam.SteamUserResponse
import com.codedisaster.steamworks.SteamAuth
import com.codedisaster.steamworks.SteamGameServer
import com.codedisaster.steamworks.SteamGameServerAPI
import com.codedisaster.steamworks.SteamID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.io.File
import java.net.ServerSocket
import java.nio.ByteBuffer

@Service
class SteamHandler(
    private val webClient: WebClient.Builder
) {
    companion object {
        private val logger = LoggerFactory.getLogger(SteamHandler::class.java)
        private val baseUrl = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/"
        private val apiKey = "CCCF25C2E631257F00C93AAED8D7037D" // Replace with your Steam API key
    }

    private lateinit var steamServer: SteamGameServer
    private lateinit var steamCallback: SteamGameServerCallbackImpl
    private var serverSteamID: SteamID? = null
    private var isLoggedOn: Boolean = false
    private var isServerRunning: Boolean = true


    // Initialize the Steam Game Server
    fun initSteamServer() {
        logger.info("Initializing Steam Game Server...")
        SteamGameServerAPI.loadLibraries()
        val gamePort: Short = 27015 // Game port for player connections
        val queryPort: Short = 27016 // Query port for Steam server list communication

        checkSteamAppIdFile()
        isPortAvailable(gamePort)
        isPortAvailable(queryPort)

        // Initialize the Steam Game Server
        val isInitialized = SteamGameServerAPI.init(
//            ip,
            0,
            gamePort,
            queryPort,
            SteamGameServerAPI.ServerMode.Authentication,
            "1.0.0"
        )
        logger.info("Steam Game Server initialized: {}", isInitialized)

        // Set up the callback implementation and pass it to the SteamGameServer
        logger.info("creating steam callback")
        steamCallback = SteamGameServerCallbackImpl(this)
        logger.info("initialising steam server")
        steamServer = SteamGameServer(steamCallback)
        logger.info("setting server data fo $steamServer")
        // Configure the server
        logger.info("name")
        steamServer.setProduct("Cultprits")
        logger.info("description")
        steamServer.setGameDescription("the one and only Cultprits server")
        logger.info("dedicated")
        steamServer.setDedicatedServer(true)

        logger.info("logging in anonymously...")
        // Log in anonymously
        steamServer.logOnAnonymous()

        logger.info("Steam Game Server initialized and attempting to log in anonymously.")
        val serverThread = Thread {
            runSteamCallbacksLoop()
        }
        serverThread.start()
    }

    fun checkSteamAppIdFile(): Boolean {
        val steamAppIdFile = File("steam_appid.txt")

        // Check if the file exists
        if (!steamAppIdFile.exists()) {
            logger.error(
                "The file 'steam_appid.txt' is missing in the working directory: {}",
                steamAppIdFile.absolutePath
            )
            return false
        }

        // Check if the file is readable
        if (!steamAppIdFile.canRead()) {
            logger.error("The file 'steam_appid.txt' exists but is not readable.")
            return false
        }

        // Verify the content of the file
        val content = steamAppIdFile.readText().trim()
        if (content.isEmpty()) {
            logger.error("The file 'steam_appid.txt' is empty.")
            return false
        }

        // Check if the content is a valid number
        val gameId = content.toLongOrNull()
        if (gameId == null || gameId <= 0) {
            logger.error("The file 'steam_appid.txt' contains invalid content: {}", content)
            return false
        }

        logger.info("The file 'steam_appid.txt' exists and contains a valid game ID: {}", gameId)
        return true
    }

    fun isPortAvailable(port: Short): Boolean {
        return try {
            // Attempt to bind to the port
            ServerSocket(port.toInt()).use { socket ->
                logger.info("Port {} is available.", port)
                true
            }
        } catch (e: Exception) {
            // Port is already in use or unavailable
            logger.error("Port {} is not available. Reason: {}", port, e.message)
            false
        }
    }


    private fun runSteamCallbacksLoop() {
        try {
            logger.info("run endless loop of steam callbacks")
            while (isServerRunning) {
                SteamGameServerAPI.runCallbacks() // Process Steamworks callbacks
                Thread.sleep(10000)
            }
        } catch (e: Exception) {
            logger.error("Error running Steam Game Server callbacks: ${e.message}")
        } finally {
            // Clean up when the thread is stopped
            SteamGameServerAPI.shutdown()
        }
    }

    fun stopSteamServer() {
        isServerRunning = false // This will terminate the loop
    }

    // Update the server SteamID when connected
    fun updateServerSteamID() {
        logger.info("Server SteamID: {}", steamServer.steamID?.toString() ?: "null")
        if (isLoggedOn) {
            logger.warn("update server steam id")
            serverSteamID = steamServer.steamID
        } else {
            logger.warn("Game server is not logged into Steam.")
        }
    }

    // Retrieve the SteamID
    fun getServerSteamID(): SteamServerIdDto? {
        return serverSteamID?.toString()?.let {
            SteamServerIdDto(
                it, it.toDecimalValue()
            )
        } ?: run {
            logger.warn("ServerSteamID is not available. Game server is not logged on.")
            null
        }
    }

    // Set the server's logged-on state
    fun setLoggedOn(loggedOn: Boolean) {
        isLoggedOn = loggedOn
        logger.info("Game server logged-on state updated: {}", loggedOn)
    }

    // Authenticate a client using the provided authentication ticket
    fun authenticateClientTicket(clientSteamID: String, authTicket: ByteArray): Boolean {
        if (!isLoggedOn) {
            logger.error("Cannot authenticate client. Game server is not logged on!")
            return false
        }

        try {
            // Parse the client's SteamID
            val steamID =
                SteamID.createFromNativeHandle(clientSteamID.toLong()) // If there's a better method, replace this.
            val authTicketBuffer = ByteBuffer.wrap(authTicket) // Auth ticket needs to be passed as a ByteBuffer

            // Begin authentication session
            val result = steamServer.beginAuthSession(authTicketBuffer, steamID)

            return when (result) {
                SteamAuth.BeginAuthSessionResult.OK -> {
                    logger.info("Client authentication successful. SteamID: {}", clientSteamID)
                    true
                }

                else -> {
                    logger.warn("Client authentication failed. SteamID: {}, Result: {}", clientSteamID, result)
                    false
                }
            }
        } catch (e: Exception) {
            logger.error("Error during client authentication. SteamID: {}, Error: {}", clientSteamID, e.message)
            return false
        }
    }

    // End the authentication session for a specific client
    fun endClientAuthSession(clientSteamID: String) {
        try {
            // Parse the client's SteamID
            val steamID = SteamID.createFromNativeHandle(clientSteamID.toLong())

            // End the authentication session
            steamServer.endAuthSession(steamID)
            logger.info("Ended authentication session for SteamID: {}", clientSteamID)
        } catch (e: Exception) {
            logger.error("Error while ending authentication session. SteamID: {}, Error: {}", clientSteamID, e.message)
        }
    }

    // Shut down the server by logging off
    fun shutdownServer() {
        logger.info("Shutting down the Steam game server...")
        steamServer.logOff()
        isLoggedOn = false
        serverSteamID = null
        logger.info("Steam game server logged off successfully.")
    }

    fun fetchUserData(steamId: String): SteamUserResponse? {
        logger.info("Fetching user data from Steam Web API for SteamID: {}", steamId)
        try {
            // Call the Steam Web API endpoint (e.g., GetPlayerSummaries)
            // Make the HTTP call
            val response = getSteamUser(steamId)!!

            // Parse API response and return as SteamUserResponse
            val steamUserResponse = parseSteamResponse(response)
            logger.info("Received valid response from Steam API for SteamID: {}", steamId)
            return steamUserResponse

        } catch (e: Exception) {
            logger.error("Error during Steam API call: {}", e.message)
            return null
        }
    }

    private fun getSteamUser(steamId: String): String? {
        return webClient.build()
            .get()
            .uri(baseUrl) {
                it.queryParam("key", apiKey)
                    .queryParam("steamids", steamId)
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
    }

    private fun parseSteamResponse(response: String): SteamUserResponse? {
        logger.info("parse Steam response: $response")
        return SteamUserResponse(
            SteamResponseBody(
                listOf(
                    PlayerData(
                        steamId = "test",
                        personaName = "test",
                        avatar = "test",
                        avatarMedium = "test",
                        avatarFull = "test",
                        profileUrl = "test",
                        lastLogOff = 0L,
                        timeCreated = 0L
                    )
                )
            )
        )
    }

    private fun String.toDecimalValue(): Long = this.toULong(16).toLong()

}


