package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUESTS_ON_START
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserQuestProgressRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import kotlin.math.min
import kotlin.random.Random

@Component
class UserQuestCreationHandler(
    private val redisUserQuestProgressRepository: RedisUserQuestProgressRepository,
) {
    companion object {
        val random: Random = Random(System.currentTimeMillis())
    }

    fun setStartsQuestsForUser(user: RedisGameUser, createdRedisQuests: List<RedisQuest>) {
        val questsWithUniqueQuestGivers = createdRedisQuests
            .shuffled(random)
            .distinctBy { it.startQuestGiverId }
        val quests =
            questsWithUniqueQuestGivers
                .shuffled(random)
                .take(min(QUESTS_ON_START, questsWithUniqueQuestGivers.size))
                .toMutableList()
        val userStartQuests = quests.map { quest ->
            RedisUserQuestProgress(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = quest.gameId,
                questId = quest.questId,
                userId = user.userId,
            )
        }
        redisUserQuestProgressRepository.saveAll(userStartQuests)
    }

}