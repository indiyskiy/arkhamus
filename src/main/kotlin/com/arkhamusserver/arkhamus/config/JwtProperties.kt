package com.arkhamusserver.arkhamus.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class JwtProperties (
    var key: String,
    var accessTokenExpiration: Long,
    var refreshTokenExpiration: Long,
)