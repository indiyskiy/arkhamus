package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty.*
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType.*
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.*
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisQuestReward
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

    fun chooseType(
        quest: RedisQuest,
        user: RedisGameUser,
        i: Int,
        previousRewards: List<RedisQuestReward>
    ): RewardType {
        val previousRewardTypes = previousRewards
            .map { it.rewardType }
            .filter { it.getOneForQuest() }
            .toSet()
        val availableByNumber = if (i < 2) {
            listOf(ITEM)
        } else {
            RewardType.values().toList()
        }
        val availableByDifficulty = availableByNumber.filter {
            availableByDifficulty(it, quest.difficulty)
        }
        val availableByPreviousTypes = availableByDifficulty.filter { it !in previousRewardTypes }
        val availableByUser = availableByPreviousTypes.filter { availableByUser(it, user) }
        return availableByUser.random(random)
    }

    private fun availableByUser(
        type: RewardType,
        user: RedisGameUser
    ): Boolean {
        return byRole(type, user.role) && byClass(type, user.classInGame)
    }

    private fun byRole(
        type: RewardType,
        role: RoleTypeInGame
    ): Boolean {
        return when (role) {
            CULTIST -> type in setOf(ITEM, REMOVE_CLUE)
            INVESTIGATOR -> type in setOf(ITEM, ADD_CLUE)
            NEUTRAL -> true
        }
    }

    private fun byClass(
        type: RewardType,
        classInGame: ClassInGame
    ): Boolean {
        return when (classInGame) {
            else -> {
                type in setOf(ITEM, ADD_CLUE, REMOVE_CLUE)
            }
        }
    }

    private fun availableByDifficulty(
        type: RewardType,
        difficulty: QuestDifficulty,
    ): Boolean {
        return when (difficulty) {
            VERY_EASY -> type in setOf(ITEM)
            EASY -> type in setOf(ITEM)
            NORMAL -> type in setOf(ITEM)
            HARD -> true
            VERY_HARD -> true
        }
    }
}