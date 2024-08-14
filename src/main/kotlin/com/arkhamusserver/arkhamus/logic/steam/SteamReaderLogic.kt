package com.arkhamusserver.arkhamus.logic.steam

import com.arkhamusserver.arkhamus.view.controller.steam.SteamCallbackController
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class SteamReaderLogic {

    companion object {
        private val logger = LoggerFactory.getLogger(SteamCallbackController::class.java)
        private const val STEAM_OPENID_URL = "https://steamcommunity.com/openid/login"
    }

    fun handleSteamAuthCallback(
        openidMode: String?,
        openidIdentity: String?,
        claimedId: String?,
        assocHandle: String?,
        signedParams: String?,
        openidSig: String?,
        opEndpoint: String?,
        baseUrl: String,
        responseNonce: String,
    ): String {
        if (openidMode == "id_res") {
            val validationURL = buildValidationURL(
                openidIdentity,
                claimedId,
                assocHandle,
                signedParams,
                openidSig,
                opEndpoint,
                baseUrl,
                responseNonce
            )

            val responseBody = Request.Post(validationURL)
                .bodyForm(
                    Form.form()
                        .add("openid.assoc_handle", assocHandle)
                        .add("openid.signed", signedParams)
                        .add("openid.sig", openidSig)
                        .build()
                )
                .execute()
                .returnContent()
                .asString()

            if (responseBody.contains("is_valid:true")) {
                val steamID = openidIdentity?.replace("https://steamcommunity.com/openid/id/", "")
                // Here you can link the steamID with your user account in the database
                logger.info("Steam ID: $steamID")
                return "Steam ID: $steamID"
            }
            logger.info("Invalid OpenID response")
            return "Invalid OpenID response."
        }
        logger.info("Invalid OpenID mode.")
        return "Invalid OpenID mode."
    }

    private fun buildValidationURL(
        openidIdentity: String?,
        claimedId: String?,
        assocHandle: String?,
        signedParams: String?,
        openidSig: String?,
        opEndpoint: String?,
        baseUrl: String,
        responseNonce: String
    ): String {
        return STEAM_OPENID_URL +
                "?openid.ns=" + URLEncoder.encode("http://specs.openid.net/auth/2.0", StandardCharsets.UTF_8.name()) +
                "&openid.mode=check_authentication" +
                "&openid.op_endpoint=" + URLEncoder.encode(opEndpoint, StandardCharsets.UTF_8.name()) +
                "&openid.claimed_id=" + URLEncoder.encode(claimedId, StandardCharsets.UTF_8.name()) +
                "&openid.identity=" + URLEncoder.encode(openidIdentity, StandardCharsets.UTF_8.name()) +
                "&openid.return_to=" + URLEncoder.encode(baseUrl, StandardCharsets.UTF_8.name()) +
                "&openid.response_nonce=" + URLEncoder.encode(responseNonce, StandardCharsets.UTF_8.name()) +
                "&openid.assoc_handle=" + URLEncoder.encode(assocHandle, StandardCharsets.UTF_8.name()) +
                "&openid.signed=" + URLEncoder.encode(signedParams, StandardCharsets.UTF_8.name()) +
                "&openid.sig=" + URLEncoder.encode(openidSig, StandardCharsets.UTF_8.name())
    }
}