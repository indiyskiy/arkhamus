package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType.*
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.ingame.InGameQuest
import com.arkhamusserver.arkhamus.model.ingame.InGameQuestReward
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class QuestRewardItemUtils {

    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    fun chooseItem(
        quest: InGameQuest,
        user: InGameUser,
        rewardType: RewardType,
        previousRewards: List<InGameQuestReward>,
        rewardsFromPreviousQuest: List<Item>
    ): Item? {
        if (rewardType != RewardType.ITEM) {
            return null
        }
        val itemsInUse: Set<Item> = previousRewards
            .filter { it.rewardType == RewardType.ITEM }
            .mapNotNull { it.rewardItem }
            .toSet()
        val itemsInUseAndPrevious: Set<Item> = itemsInUse.plus(rewardsFromPreviousQuest)
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
            it !in itemsInUseAndPrevious
        }.ifEmpty {
            itemsOfType
                .filter { it !in itemsInUse }
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
            setOf(LOOT, CULTIST_LOOT, RARE_LOOT)
        } else {
            setOf(LOOT, RARE_LOOT)
        }
    }

    private fun excellentLootTypes(role: RoleTypeInGame): Set<ItemType> {
        return if (role == RoleTypeInGame.CULTIST) {
            setOf(
                LOOT,
                CULTIST_LOOT,
                RARE_LOOT,
                USEFUL_ITEM,
                CULTIST_ITEM
            )
        } else {
            setOf(
                LOOT,
                RARE_LOOT,
                USEFUL_ITEM
            )
        }
    }
}