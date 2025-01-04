package com.arkhamusserver.arkhamus.config.database.levelDesign.clues

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues.ScentClueRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.ScentClue
import com.arkhamusserver.arkhamus.view.levelDesign.ScentClueFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignScentClueInfoProcessor(
    private val scentClueRepository: ScentClueRepository
) {
    fun processClueInfos(scentClues: List<ScentClueFromJson>, savedLevel: Level) {
        scentClues.forEach { scentClue ->
            ScentClue(
                x = scentClue.x,
                y = scentClue.y,
                z = scentClue.z,
                inGameId = scentClue.id,
                interactionRadius = scentClue.interactionRadius,
                name = scentClue.name,
                level = savedLevel,
            ).apply {
                scentClueRepository.save(this)
            }
        }
    }
}