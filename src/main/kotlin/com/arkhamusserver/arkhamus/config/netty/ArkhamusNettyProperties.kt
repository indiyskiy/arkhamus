package com.arkhamusserver.arkhamus.config.netty

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "netty")
data class ArkhamusNettyProperties(
    @NotNull
    @Size(min = 1000, max = 65535)
    var tcpPort: Int = 0,

    @NotNull
    @Min(1)
    var bossCount: Int = 0,

    @NotNull
    @Min(2)
    var workerCount: Int = 0,

    @NotNull
    var keepAlive: Boolean = false,

    @NotNull
    var backlog: Int = 0
)