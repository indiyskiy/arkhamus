package com.arkhamusserver.arkhamus.config.database.levelDesign.clues

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.DistortionClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.DistortionClue
import com.arkhamusserver.arkhamus.view.levelDesign.JsonDistortionClue
import org.springframework.stereotype.Component

@Component
class LevelDesignDistortionClueInfoProcessor(
    private val distortionClueRepository: DistortionClueRepository
) {
    fun processDistortionClueInfos(distortionClues: List<JsonDistortionClue>, savedLevel: Level) {
        distortionClues.forEach { distortionClue ->
            DistortionClue(
                x = distortionClue.x!!,
                y = distortionClue.y!!,
                z = distortionClue.z!!,
                inGameId = distortionClue.id!!,
                interactionRadius = distortionClue.interactionRadius!!,
                level = savedLevel,
                canReceive = distortionClue.canReceive!!,
                canTransmit = distortionClue.canTransmit!!,
                effectRadius = distortionClue.effectRadius!!,
            ).apply {
                distortionClueRepository.save(this)
            }
        }
    }
}