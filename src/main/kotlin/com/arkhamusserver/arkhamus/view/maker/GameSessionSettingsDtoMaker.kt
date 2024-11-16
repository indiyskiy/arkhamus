package com.arkhamusserver.arkhamus.view.maker

import com.arkhamusserver.arkhamus.model.database.entity.GameSessionSettings
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.view.dto.GameSessionSettingsDto
import org.springframework.stereotype.Component

@Component
class GameSessionSettingsDtoMaker(
    val levelDtoMaker: LevelDtoMaker
) {
    fun toDto(gameSessionSettings: GameSessionSettings): GameSessionSettingsDto {
        return GameSessionSettingsDto(
            lobbySize = gameSessionSettings.lobbySize,
            numberOfCultists = gameSessionSettings.numberOfCultists
        ).apply {
            gameSessionSettings.level?.let { level ->
                this.level = levelDtoMaker.mapLevelToDto(level)
            }
        }
    }

    fun merge(gameSettings: GameSessionSettings, level: Level?, gameSessionSettingsDto: GameSessionSettingsDto) {
        gameSettings.lobbySize = gameSessionSettingsDto.lobbySize
        gameSettings.numberOfCultists = gameSessionSettingsDto.numberOfCultists
        gameSettings.level = level
    }
}