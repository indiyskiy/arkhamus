package com.arkhamusserver.arkhamus.config.database.levelDesign.clues

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.AuraClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelZone
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.AuraClue
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.view.levelDesign.JsonAuraClue
import org.springframework.stereotype.Component

@Component
class LevelDesignAuraClueInfoProcessor(
    private val auraClueRepository: AuraClueRepository,
) {
    fun processAuraClueInfos(
        auraCluesJsons: List<JsonAuraClue>,
        savedLevel: Level,
        zones: List<LevelZone>
    ) {
        val auraZones = zones.filter {
            it.zoneType == ZoneType.AURA
        }.associateBy { it.inGameId }

        auraCluesJsons.forEach { auraClueJson: JsonAuraClue ->
            AuraClue(
                x = auraClueJson.x!!,
                y = auraClueJson.y!!,
                z = auraClueJson.z!!,
                inGameId = auraClueJson.id!!,
                level = savedLevel,
                zone = auraZones[auraClueJson.zoneId]!!,
                minSpawnRadius = auraClueJson.minSpawnRadius!!,
                maxSpawnRadius = auraClueJson.maxSpawnRadius!!,
                interactionRadius = auraClueJson.interactionRadius!!,
            ).let {
                auraClueRepository.save(it)
            }
        }
    }
}