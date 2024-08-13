package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.model.enums.ingame.*
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
        i: Int,
        previousRewards: List<RedisQuestReward>
    ): Item? {
        if (rewardType != RewardType.ITEM) {
            return null
        }
        val itemsInUse = previousRewards
            .filter { it.rewardType == RewardType.ITEM }
            .mapNotNull { it.rewardItem }
            .toSet()
        val possibleSet = if (i > 0) {
            if (i > 1) {
                if (quest.difficulty in setOf(QuestDifficulty.VERY_EASY, QuestDifficulty.EASY)) {
                    goodLoot(user.role)
                } else {
                    excellentLootTypes(user.role)
                }
            } else {
                if (quest.difficulty in setOf(QuestDifficulty.VERY_EASY, QuestDifficulty.EASY)) {
                    mediumLoot(user.role)
                } else {
                    goodLoot(user.role)
                }
            }
        } else {
            if (quest.difficulty in setOf(QuestDifficulty.VERY_EASY, QuestDifficulty.EASY)) {
                simpleLoot(user.role)
            } else {
                mediumLoot(user.role)
            }
        }
        val type = possibleSet.random(random)
        val item = Item
            .values()
            .filter { it.itemType == type }
            .filter { it.id !in itemsInUse }
            .random(random)
        return item
    }


    private fun simpleLoot(role: RoleTypeInGame): Set<ItemType> {
        return if (role == RoleTypeInGame.CULTIST) {
            setOf(ItemType.LOOT, ItemType.CULTIST_LOOT)
        } else {
            setOf(ItemType.LOOT)
        }
    }

    private fun mediumLoot(role: RoleTypeInGame): Set<ItemType> {
        return if (role == RoleTypeInGame.CULTIST) {
            setOf(ItemType.LOOT, ItemType.CULTIST_LOOT, ItemType.RARE_LOOT)
        } else {
            setOf(ItemType.LOOT, ItemType.RARE_LOOT)
        }
    }

    private fun goodLoot(role: RoleTypeInGame): Set<ItemType> {
        return if (role == RoleTypeInGame.CULTIST) {
            setOf(ItemType.LOOT, ItemType.CULTIST_LOOT, ItemType.RARE_LOOT, ItemType.CRAFT_T2)
        } else {
            setOf(ItemType.LOOT, ItemType.RARE_LOOT, ItemType.CRAFT_T2)
        }
    }

    private fun excellentLootTypes(role: RoleTypeInGame): Set<ItemType> {
        return if (role == RoleTypeInGame.CULTIST) {
            setOf(ItemType.LOOT, ItemType.CULTIST_LOOT, ItemType.RARE_LOOT, ItemType.CRAFT_T2, ItemType.USEFUL_ITEM)
        } else {
            setOf(ItemType.LOOT, ItemType.RARE_LOOT, ItemType.CRAFT_T2, ItemType.USEFUL_ITEM, ItemType.CULTIST_ITEM)
        }
    }
}