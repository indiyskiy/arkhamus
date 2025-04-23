package com.arkhamusserver.arkhamus.view.controller.admin.browser.tech.auth

import com.arkhamusserver.arkhamus.config.auth.JwtProperties
import com.arkhamusserver.arkhamus.logic.auth.AuthenticationService
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationRequest
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
class AuthBrowserController(
    private val authenticationService: AuthenticationService,
    private val jwtProperties: JwtProperties
) {

    companion object {
        private val logger = LoggingUtils.getLogger<AuthBrowserController>()
    }

    @PostMapping(
        "/public/browser/auth",
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun authenticate(
        @ModelAttribute loginInfo: AuthenticationRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        LoggingUtils.withContext(
            eventType = LoggingUtils.EVENT_STEAM
        ) {
            logger.info("gogo authenticate!")
        }
        val authResponse = authenticationService.authentication(loginInfo)
        val cookie = Cookie("token", authResponse.accessToken)
        cookie.maxAge = jwtProperties.cookieMaxAgeSeconds
        cookie.isHttpOnly = true
        cookie.path = "/"
        response.addCookie(cookie)
        return "menu"
    }
}
