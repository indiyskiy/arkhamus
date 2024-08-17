package com.arkhamusserver.arkhamus.logic.steam

import com.arkhamusserver.arkhamus.logic.exception.ArkhamusServerRequestException
import com.arkhamusserver.arkhamus.view.controller.steam.SteamAuthController
import com.arkhamusserver.arkhamus.view.dto.steam.SteamUserResponse.SteamUserResponse
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.net.URLEncoder

@Component
class SteamLogic {
    companion object {
        private const val STEAM_URL = "https://steamcommunity.com"
        private const val STEAM_PRIVATE_URL = "https://api.steampowered.com"

        private const val STEAM_OPENID_URL = "$STEAM_URL/openid/login"
        private const val STEAM_WEB_API = "$STEAM_PRIVATE_URL/ISteamUserAuth/AuthenticateUserTicket/v1/"
        private const val CULTPRITS_REALM = "http://cultprits.com"
        private const val CULTPRITS_CALLBACK = "${CULTPRITS_REALM}/steam/callback"

        private const val STEAM_API_KEY = "CCCF25C2E631257F00C93AAED8D7037D"
        private const val STEAM_GAME_ID = "480" //public game id

        private val STEAM_USER_INFO = "$STEAM_PRIVATE_URL/ISteamUser/GetPlayerSummaries/v2/"

        private val logger = LoggerFactory.getLogger(SteamAuthController::class.java)
    }

    private val gson = Gson()

    fun readStaemUserData(steamId: String): SteamUserResponse {
        val restTemplate = RestTemplate()
        val url = "$STEAM_USER_INFO?key=${STEAM_API_KEY}&steamids=$steamId"
        val response = restTemplate.getForEntity(url, String::class.java)

        if (response.statusCode.is2xxSuccessful) {
            logger.info("steam user response: ${response.body}")
            val steamUserResponse: SteamUserResponse = gson.fromJson(response.body, SteamUserResponse::class.java)
            return steamUserResponse
        } else {
            throw ArkhamusServerRequestException(
                "can't resolve steam id by steam server",
                SteamUserResponse::class.java.simpleName
            )
        }
    }


    @OptIn(ExperimentalUnsignedTypes::class)
    fun authFromClient(authTicket: ByteArray): ResponseEntity<String> {
        val params = mapOf(
            "key" to STEAM_API_KEY,
            "appid" to STEAM_GAME_ID,
            "ticket" to authTicket.toUByteArray().joinToString(separator = "") { it.toString(16).padStart(2, '0') }
        )

        val restTemplate = RestTemplate()
        val responseFromSteam = restTemplate.postForEntity(STEAM_WEB_API, params, String::class.java)
        if (responseFromSteam.statusCode == HttpStatus.OK) {
            logger.info("Steam-from-client auth successful: ${responseFromSteam.body}")
            ResponseEntity.ok("Steam ID: ${responseFromSteam.body}")
        } else {
            logger.info("Steam auth failed: ${responseFromSteam.body}")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid auth ticket")
        }
        return responseFromSteam
    }

    fun buildSteamAuthUrl(): String {
        return STEAM_OPENID_URL +
                "?openid.ns=http://specs.openid.net/auth/2.0" +
                "&openid.mode=checkid_setup" +
                "&openid.return_to=" + URLEncoder.encode(CULTPRITS_CALLBACK, "UTF-8") +
                "&openid.realm=" + URLEncoder.encode(CULTPRITS_CALLBACK, "UTF-8") +
                "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select" +
                "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select"
    }

}