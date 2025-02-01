package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameQuest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.max
import kotlin.random.Random

@Component
class QuestRewardAmountUtils {
    companion object {
        const val DEFAULT_AMOUNT = 5
        private val random = Random(System.currentTimeMillis())
        var logger: Logger = LoggerFactory.getLogger(QuestRewardAmountUtils::class.java)
    }

    fun chooseAmount(
        quest: InGameQuest,
        user: InGameUser,
        rewardType: RewardType,
        rewardItem: Item?,
    ): Int {
        if (rewardItem == null || rewardType != RewardType.ITEM) {
            return 0
        }
        val modifier: Double =
            luckComponent(user) * difficultyModifier(quest) * rewardItemModifier(rewardItem) * userModifier(user)
        return if (modifier < 0.1) {
            1
        } else {
            max(1, (DEFAULT_AMOUNT * modifier).toInt())
        }
    }


    private fun luckComponent(user: InGameUser): Double {
        return 1.0 + (0.1 * random.nextInt(3 + userLuck(user)))
    }

    private fun userLuck(user: InGameUser): Int {
        return 0
    }

    private fun userModifier(user: InGameUser): Double {
        return when (user.role) {
            RoleTypeInGame.CULTIST -> 1.0
            RoleTypeInGame.INVESTIGATOR -> 1.0
            RoleTypeInGame.NEUTRAL -> 1.2
        }
    }

    private fun rewardItemModifier(rewardItem: Item): Double {
        return when (rewardItem.itemType) {
            ItemType.LOOT -> 1.0
            ItemType.RARE_LOOT -> 0.2
            ItemType.CULTIST_LOOT -> 0.7
            ItemType.INVESTIGATION -> 0.0
            ItemType.USEFUL_ITEM -> 0.0
            ItemType.CULTIST_ITEM -> 0.0
            ItemType.ADVANCED_USEFUL_ITEM -> 0.0
            ItemType.ADVANCED_CULTIST_ITEM -> 0.0
            else -> 0.0
        }
    }

    private fun difficultyModifier(quest: InGameQuest): Double {
        return when (quest.difficulty) {
            QuestDifficulty.VERY_EASY -> 0.25
            QuestDifficulty.EASY -> 0.5
            QuestDifficulty.NORMAL -> 1.0
            QuestDifficulty.HARD -> 1.7
            QuestDifficulty.VERY_HARD -> 2.4
        }
    }
}