package com.arkhamusserver.arkhamus.config.database.levelDesign.clues

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.CorruptionClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.CorruptionClue
import com.arkhamusserver.arkhamus.view.levelDesign.JsonCorruptionClue
import org.springframework.stereotype.Component

@Component
class LevelDesignCorruptionClueInfoProcessor(
    private val corruptionClueRepository: CorruptionClueRepository
) {
    fun processCorruptionClueInfos(corruptionClues: List<JsonCorruptionClue>, savedLevel: Level) {
        corruptionClues.forEach { corruptionClue ->
            CorruptionClue(
                x = corruptionClue.x!!,
                y = corruptionClue.y!!,
                z = corruptionClue.z!!,
                inGameId = corruptionClue.id!!,
                interactionRadius = corruptionClue.interactionRadius!!,
                name = corruptionClue.name!!,
                level = savedLevel,
            ).apply {
                corruptionClueRepository.save(this)
            }
        }
    }
}