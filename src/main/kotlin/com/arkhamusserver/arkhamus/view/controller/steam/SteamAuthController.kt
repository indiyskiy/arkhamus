package com.arkhamusserver.arkhamus.view.controller.steam

import com.arkhamusserver.arkhamus.logic.steam.SteamLogic
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/steam/auth")
class SteamAuthController(
    private val steamLogic: SteamLogic
) {

    @PostMapping("verifySteamTicket")
    fun verifySteamTicket(
        @RequestBody authTicket: ByteArray
    ): ResponseEntity<String> {
        val responseFromSteam = steamLogic.authFromClient(authTicket)
        return if (responseFromSteam.statusCode == HttpStatus.OK) {
            ResponseEntity.ok("Steam ID: ${responseFromSteam.body}")
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid auth ticket")
        }
    }

    @GetMapping("redirect")
    fun redirectToSteamLogin(request: HttpServletRequest, response: HttpServletResponse) {
        val requestUrl = steamLogic.buildSteamAuthUrl()
        response.sendRedirect(requestUrl)
    }
}