package com.arkhamusserver.arkhamus.logic.steam

import com.arkhamusserver.arkhamus.logic.exception.ArkhamusServerRequestException
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
        private const val STEAM_URL = "https://steamcommunity.com"
        private const val STEAM_OPENID_URL = "${STEAM_URL}/openid/login"
        private const val STEAM_ID_URL = "${STEAM_URL}/openid/id/"
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
        logger.info("OpenID Fields:")
        logger.info("openidMode: {}", openidMode)
        logger.info("openidIdentity: {}", openidIdentity)
        logger.info("claimedId: {}", claimedId)
        logger.info("assocHandle: {}", assocHandle)
        logger.info("signedParams: {}", signedParams)
        logger.info("openidSig: {}", openidSig)
        logger.info("opEndpoint: {}", opEndpoint)
        logger.info("baseUrl: {}", baseUrl)
        logger.info("responseNonce: {}", responseNonce)
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

            logger.info("responseBody: ${responseBody}")

            if (responseBody.contains("is_valid:true")) {
                val steamId = openidIdentity?.replace(STEAM_ID_URL, "")
                logger.info("Steam Id: $steamId")
                return "Steam Id: $steamId"
            }
           throw ArkhamusServerRequestException("Invalid OpenID response", "SteamAuthCallback")
        }
        throw ArkhamusServerRequestException("Invalid OpenId mode", "SteamAuthCallback")
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