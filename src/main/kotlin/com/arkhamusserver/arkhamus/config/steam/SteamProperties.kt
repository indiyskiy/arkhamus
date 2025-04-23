package com.arkhamusserver.arkhamus.config.steam

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "steam")
data class SteamProperties(
    @NotBlank
    var apiBaseUrl: String = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/",
    
    @NotBlank
    var publicApiKey: String = "",
    
    @NotBlank
    var secretApiKey: String = "",
    
    @NotNull
    @Min(1)
    var gameId: Int = 0,
    
    @NotNull
    @Min(1)
    var gamePort: Short = 0,
    
    @NotNull
    @Min(1)
    var queryPort: Short = 0,
    
    @NotBlank
    var serverVersion: String = "1.0.0",
    
    @NotBlank
    var productName: String = "",
    
    @NotBlank
    var gameDescription: String = "",
    
    @NotNull
    @Min(1000)
    var callbackIntervalMs: Long = 10000
)