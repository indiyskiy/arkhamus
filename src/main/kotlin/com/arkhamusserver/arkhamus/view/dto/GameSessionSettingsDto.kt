package com.arkhamusserver.arkhamus.view.dto

data class GameSessionSettingsDto(
    var lobbySize: Int,
    var numberOfCultists: Int,
    var level: LevelDto? = null
)