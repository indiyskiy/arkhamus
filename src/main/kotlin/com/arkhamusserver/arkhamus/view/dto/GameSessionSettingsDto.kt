package com.arkhamusserver.arkhamus.view.dto

import com.arkhamusserver.arkhamus.view.dto.ingame.ClassInGameDto

data class GameSessionSettingsDto(
    var lobbySize: Int,
    var numberOfCultists: Int,
    var availableClasses: List<ClassInSettingsDto>?,
    var level: LevelDto? = null
)

data class ClassInSettingsDto(
    val classInGame: ClassInGameDto,
    val turnedOnForGame: Boolean
)