package com.arkhamusserver.arkhamus.view.maker

import com.arkhamusserver.arkhamus.logic.ingame.ClassInGameLogic
import com.arkhamusserver.arkhamus.model.database.entity.GameSessionSettings
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toClassInGame
import com.arkhamusserver.arkhamus.view.dto.GameSessionSettingsDto
import com.arkhamusserver.arkhamus.view.maker.ingame.ClassInGameDtoMaker
import org.springframework.stereotype.Component

@Component
class GameSessionSettingsDtoMaker(
    val levelDtoMaker: LevelDtoMaker,
    val classInGameDtoMaker: ClassInGameDtoMaker,
    val classInGameLogic: ClassInGameLogic
) {
    fun toDto(gameSessionSettings: GameSessionSettings): GameSessionSettingsDto {
        return GameSessionSettingsDto(
            lobbySize = gameSessionSettings.lobbySize,
            numberOfCultists = gameSessionSettings.numberOfCultists,
            availableClasses = gameSessionSettings.classesInGame.map {
                classInGameDtoMaker.convert(it, classInGameLogic.resolveAbility(it))
            }
        ).apply {
            gameSessionSettings.level?.let { level ->
                this.level = levelDtoMaker.mapLevelToDto(level)
            }
        }
    }

    fun merge(gameSettings: GameSessionSettings, level: Level?, gameSessionSettingsDto: GameSessionSettingsDto) {
        gameSettings.lobbySize = gameSessionSettingsDto.lobbySize
        gameSettings.numberOfCultists = gameSessionSettingsDto.numberOfCultists
        gameSettings.classesInGame =
            gameSessionSettingsDto.availableClasses
                .mapNotNull { it.id.toClassInGame() }
                .filter { it.turnedOn }
                .toSet()
        gameSettings.level = level
    }
}