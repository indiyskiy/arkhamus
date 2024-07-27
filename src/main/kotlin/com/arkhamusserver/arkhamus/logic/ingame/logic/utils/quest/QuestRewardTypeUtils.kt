package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class QuestRewardTypeUtils {

    companion object {
        private val random = Random(System.currentTimeMillis())
        var logger: Logger = LoggerFactory.getLogger(QuestRewardTypeUtils::class.java)
    }

    fun chooseType(quest: RedisQuest, user: RedisGameUser, i: Int): RewardType {
        return if (i < 2 || quest.difficulty !in setOf(QuestDifficulty.HARD, QuestDifficulty.VERY_HARD)) {
            RewardType.ITEM
        } else {
            if (quest.difficulty != QuestDifficulty.VERY_HARD) {
                RewardType.values().random(random)
            } else {
                RewardType.values().filter { it != RewardType.ITEM }.random(random)
            }
        }
    }
}