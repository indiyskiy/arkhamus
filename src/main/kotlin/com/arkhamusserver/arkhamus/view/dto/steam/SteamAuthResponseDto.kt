package com.arkhamusserver.arkhamus.view.dto.steam

data class SteamAuthResponseDto (
    val success: Boolean,             // Whether the auth succeeded or not
    val errorMessage: String?,        // Description of any error (null if successful)
    val additionalData: Any? // Optional: any additional data about the player (permissions, bans, etc.)
)