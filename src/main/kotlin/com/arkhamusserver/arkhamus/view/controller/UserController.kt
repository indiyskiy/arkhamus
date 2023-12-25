package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationRequest
import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationResponse
import com.arkhamusserver.arkhamus.model.dataaccess.auth.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/auth")
class UserController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping
    fun authenticate(
        @RequestBody authRequest: AuthenticationRequest
    ): AuthenticationResponse =
        authenticationService.authentication(authRequest)
}