package com.arkhamusserver.arkhamus.view.controller.steam

import com.arkhamusserver.arkhamus.logic.auth.SteamAuthService
import com.arkhamusserver.arkhamus.logic.steam.SteamReaderLogic
import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/steam/callback")
class SteamCallbackController(
    private val steamReaderLogic: SteamReaderLogic,
    private val steamAuthService: SteamAuthService,
) {

    @GetMapping
    fun handleSteamCallback(
        request: HttpServletRequest,
        @RequestParam("openid.mode", required = false) openidMode: String?,
        @RequestParam("openid.identity", required = false) openidIdentity: String?,
        @RequestParam("openid.claimed_id", required = false) claimedId: String?,
        @RequestParam("openid.assoc_handle", required = false) assocHandle: String?,
        @RequestParam("openid.signed", required = false) signedParams: String?,
        @RequestParam("openid.sig", required = false) openidSig: String?,
        @RequestParam("openid.op_endpoint", required = false) opEndpoint: String?,
        @RequestParam("openid.response_nonce") responseNonce: String,
    ): AuthenticationResponse {
        val baseUrl = request.requestURL.toString()
        val steamId = steamReaderLogic.handleSteamAuthCallback(
            openidMode,
            openidIdentity,
            claimedId,
            assocHandle,
            signedParams,
            openidSig,
            opEndpoint,
            baseUrl,
            responseNonce
        )
        return steamAuthService.authenticationSteam(steamId)
    }
}