package com.arkhamusserver.arkhamus.config.auth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class JwtProperties(
    val key: String = "",
    val access: Long = 0L,
    val refresh: Long = 0L,
    val cookieMaxAgeSeconds: Int = 86400, // 1 day in seconds
)
