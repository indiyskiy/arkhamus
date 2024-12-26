package com.arkhamusserver.arkhamus.view.maker

import com.arkhamusserver.arkhamus.logic.ingame.ClassInGameLogic
import com.arkhamusserver.arkhamus.model.database.entity.GameSessionSettings
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toClassInGame
import com.arkhamusserver.arkhamus.view.dto.ClassInSettingsDto
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
        val settingClasses = gameSessionSettings.classesInGame.filter { it.globalTurnedOn }.map { it.id }
        val availableClasses = ClassInGame.values().map {
            classInGameDtoMaker.convert(it, classInGameLogic.resolveAbility(it))
        }
        return GameSessionSettingsDto(
            lobbySize = gameSessionSettings.lobbySize,
            numberOfCultists = gameSessionSettings.numberOfCultists,
            availableClasses = availableClasses.map {
                ClassInSettingsDto(
                    classInGame = it,
                    turnedOnForGame = settingClasses.contains(it.id)
                )
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
        val classes: Collection<ClassInGame> = gameSessionSettingsDto.availableClasses
            ?.filter { it.turnedOnForGame }
            ?.mapNotNull { it.classInGame.id.toClassInGame() } ?: ClassInGame.values().toSet()
        gameSettings.classesInGame = classes.filter { it.globalTurnedOn }.toSet()
        gameSettings.level = level
    }
}