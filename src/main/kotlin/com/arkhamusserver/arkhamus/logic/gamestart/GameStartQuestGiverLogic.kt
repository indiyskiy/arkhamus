package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameQuestGiverRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestGiverRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameQuestGiver
import org.springframework.stereotype.Component

@Component
class GameStartQuestGiverLogic(
    private val questGiverRepository: QuestGiverRepository,
    private val inGameQuestGiverRepository: InGameQuestGiverRepository
) {
    fun createQuestGivers(levelId: Long, game: GameSession) {
        val questGivers = questGiverRepository.findByLevelId(levelId)
        val inGameQuestGivers = questGivers.map {
            InGameQuestGiver(
                id = generateRandomId(),
                gameId = game.id!!,
                questGiverId = it.inGameId,
                state = MapObjectState.ACTIVE,
                x = it.x,
                y = it.y,
                z = it.z,
                interactionRadius = it.interactionRadius,
                gameTags = mutableSetOf(),
                visibilityModifiers = setOf(VisibilityModifier.ALL)
            )
        }
        inGameQuestGiverRepository.saveAll(inGameQuestGivers)
    }

}