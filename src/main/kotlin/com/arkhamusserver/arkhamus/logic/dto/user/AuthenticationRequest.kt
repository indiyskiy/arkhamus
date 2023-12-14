package com.arkhamusserver.arkhamus.logic.dto.user

data class AuthenticationRequest(
    var login: String,
    var password: String,
)