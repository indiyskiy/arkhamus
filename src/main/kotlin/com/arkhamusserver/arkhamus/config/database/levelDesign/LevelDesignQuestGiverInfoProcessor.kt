package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestGiverRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.QuestGiver
import com.arkhamusserver.arkhamus.view.levelDesign.QuestGiverFromJson
import org.postgresql.geometric.PGpoint
import org.springframework.stereotype.Component

@Component
class LevelDesignQuestGiverInfoProcessor(
    private val questGiverRepository: QuestGiverRepository,
) {
    fun processQuestGiverFromJson(questGivers: List<QuestGiverFromJson>, savedLevel: Level): List<QuestGiver> {
        questGivers.map { jsonQuestGiver ->
            QuestGiver(
                inGameId = jsonQuestGiver.id!!,
                point = PGpoint(jsonQuestGiver.x!!, jsonQuestGiver.y!!),
                interactionRadius = jsonQuestGiver.interactionRadius!!,
                level = savedLevel,
                name = jsonQuestGiver.name!!
            )
        }.apply {
            return questGiverRepository.saveAll(this).toList()
        }
    }
}