package com.arkhamusserver.arkhamus.view.dto.user

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)