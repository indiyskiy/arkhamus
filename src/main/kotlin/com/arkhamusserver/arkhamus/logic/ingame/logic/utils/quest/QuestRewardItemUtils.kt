package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.model.enums.ingame.*
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType.*
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisQuestReward
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class QuestRewardItemUtils {

    companion object {
        private val random = Random(System.currentTimeMillis())
        var logger: Logger = LoggerFactory.getLogger(QuestRewardItemUtils::class.java)
    }

    fun chooseItem(
        quest: RedisQuest,
        user: RedisGameUser,
        rewardType: RewardType,
        previousRewards: List<RedisQuestReward>,
        rewardsFromPreviousQuest: List<Int>
    ): Item? {
        if (rewardType != RewardType.ITEM) {
            return null
        }
        val itemsInUse = previousRewards
            .filter { it.rewardType == RewardType.ITEM }
            .mapNotNull { it.rewardItem }
            .toSet()
        val itemsInUseAndPrevious = itemsInUse.plus(rewardsFromPreviousQuest)
        val possibleSet = when (quest.difficulty) {
            QuestDifficulty.VERY_EASY -> simpleLoot(user.role)
            QuestDifficulty.EASY -> simpleLoot(user.role)
            QuestDifficulty.NORMAL -> mediumLoot(user.role)
            QuestDifficulty.HARD -> goodLoot(user.role)
            QuestDifficulty.VERY_HARD -> excellentLootTypes(user.role)
        }
        val type = possibleSet.random(random)
        val itemsOfType = Item
            .values()
            .filter { it.itemType == type }
        return itemsOfType.filter {
            it.id !in itemsInUseAndPrevious
        }.ifEmpty {
            itemsOfType
                .filter { it.id !in itemsInUse }
        }.ifEmpty {
            itemsOfType
        }.random(random)
    }


    private fun simpleLoot(role: RoleTypeInGame): Set<ItemType> {
        return if (role == RoleTypeInGame.CULTIST) {
            setOf(LOOT, CULTIST_LOOT)
        } else {
            setOf(LOOT)
        }
    }

    private fun mediumLoot(role: RoleTypeInGame): Set<ItemType> {
        return if (role == RoleTypeInGame.CULTIST) {
            setOf(LOOT, CULTIST_LOOT, RARE_LOOT)
        } else {
            setOf(LOOT, RARE_LOOT)
        }
    }

    private fun goodLoot(role: RoleTypeInGame): Set<ItemType> {
        return if (role == RoleTypeInGame.CULTIST) {
            setOf(LOOT, CULTIST_LOOT, RARE_LOOT, CRAFT_T2)
        } else {
            setOf(LOOT, RARE_LOOT, CRAFT_T2)
        }
    }

    private fun excellentLootTypes(role: RoleTypeInGame): Set<ItemType> {
        return if (role == RoleTypeInGame.CULTIST) {
            setOf(
                LOOT,
                CULTIST_LOOT,
                RARE_LOOT,
                CRAFT_T2,
                USEFUL_ITEM,
                CULTIST_ITEM
            )
        } else {
            setOf(
                LOOT,
                RARE_LOOT,
                CRAFT_T2,
                USEFUL_ITEM
            )
        }
    }
}