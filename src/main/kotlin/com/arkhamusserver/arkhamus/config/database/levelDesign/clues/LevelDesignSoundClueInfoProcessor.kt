package com.arkhamusserver.arkhamus.config.database.levelDesign.clues

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.SoundClueJammerRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.SoundClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelZone
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.SoundClue
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.SoundClueJammer
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.view.levelDesign.JsonSoundClue
import com.arkhamusserver.arkhamus.view.levelDesign.JsonSoundClueJammer
import org.springframework.stereotype.Component

@Component
class LevelDesignSoundClueInfoProcessor(
    private val soundClueRepository: SoundClueRepository,
    private val soundClueJammerRepository: SoundClueJammerRepository,
) {
    fun processSoundInfos(
        soundCluesJson: List<JsonSoundClue>,
        soundClueJammersJson: List<JsonSoundClueJammer>,
        savedLevel: Level,
        zones: List<LevelZone>
    ) {
        val soundZones = zones.filter {
            it.zoneType == ZoneType.SOUND
        }.associateBy { it.inGameId }

        val soundClues = soundCluesJson.map { soundClueJson ->
            SoundClue(
                x = soundClueJson.x!!,
                y = soundClueJson.y!!,
                z = soundClueJson.z!!,
                inGameId = soundClueJson.id!!,
                level = savedLevel,
                zone = soundZones[soundClueJson.zoneId]!!,
            ).let {
                soundClueRepository.save(it)
            }
        }
        soundClues.forEach { soundClue ->
            val soundClueJsonJammers = soundClueJammersJson.filter {
                it.relatedClue == soundClue.inGameId
            }
            soundClueJsonJammers.forEach { soundClueJsonJammer ->
                SoundClueJammer(
                    interactionRadius = soundClueJsonJammer.interactionRadius!!,
                    name = soundClueJsonJammer.name!!,
                    relatedClue = soundClue,
                    x = soundClueJsonJammer.x!!,
                    y = soundClueJsonJammer.y!!,
                    z = soundClueJsonJammer.z!!,
                    inGameId = soundClueJsonJammer.id!!,
                    level = savedLevel,
                ).apply {
                    soundClueJammerRepository.save(this)
                }
            }
        }
    }
}