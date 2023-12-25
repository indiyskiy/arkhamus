package com.arkhamusserver.arkhamus.view.dto.user

data class AuthenticationRequest(
    var login: String,
    var password: String,
)