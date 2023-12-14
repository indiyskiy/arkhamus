package com.arkhamusserver.arkhamus.logic.dto.user

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
)