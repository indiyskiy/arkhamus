package com.arkhamusserver.arkhamus.view.maker

import com.arkhamusserver.arkhamus.model.database.entity.GameSessionSettings
import com.arkhamusserver.arkhamus.model.database.entity.Level
import com.arkhamusserver.arkhamus.view.dto.GameSessionSettingsDto
import com.arkhamusserver.arkhamus.view.dto.LevelDto
import org.springframework.stereotype.Component

@Component
class GameSessionSettingsDtoMaker {
    fun toDto(gameSessionSettings: GameSessionSettings): GameSessionSettingsDto {
        return GameSessionSettingsDto(
            lobbySize = gameSessionSettings.lobbySize,
            numberOfCultists = gameSessionSettings.numberOfCultists
        ).apply {
            gameSessionSettings.level?.let { level ->
                this.level = LevelDto().apply {
                    this.levelId = level.id
                    this.version = level.version
                }
            }
        }
    }

    fun merge(gameSettings: GameSessionSettings, level: Level?, gameSessionSettingsDto: GameSessionSettingsDto) {
        gameSettings.lobbySize = gameSessionSettingsDto.lobbySize
        gameSettings.numberOfCultists = gameSessionSettingsDto.numberOfCultists
        gameSettings.level = level
    }
}