package com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestGiverRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.QuestGiver
import com.arkhamusserver.arkhamus.view.levelDesign.QuestGiverFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignQuestGiverInfoProcessor(
    private val questGiverRepository: QuestGiverRepository,
) {
    fun processQuestGiverFromJson(questGivers: List<QuestGiverFromJson>, savedLevel: Level): List<QuestGiver> {
        questGivers.map { jsonQuestGiver ->
            QuestGiver(
                inGameId = jsonQuestGiver.id!!,
                x = jsonQuestGiver.x!!,
                y = jsonQuestGiver.y!!,
                z = jsonQuestGiver.z!!,
                interactionRadius = jsonQuestGiver.interactionRadius!!,
                level = savedLevel,
                name = jsonQuestGiver.name!!
            )
        }.apply {
            return questGiverRepository.saveAll(this).toList()
        }
    }
}