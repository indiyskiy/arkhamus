package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisQuestGiverRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestGiverRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisQuestGiver
import org.springframework.stereotype.Component

@Component
class GameStartQuestGiverLogic(
    private val questGiverRepository: QuestGiverRepository,
    private val redisQuestGiverRepository: RedisQuestGiverRepository
) {
    fun createQuestGivers(levelId: Long, game: GameSession) {
        val questGivers = questGiverRepository.findByLevelId(levelId)
        val redisQuestGivers = questGivers.map {
            RedisQuestGiver(
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
        redisQuestGiverRepository.saveAll(redisQuestGivers)
    }

}