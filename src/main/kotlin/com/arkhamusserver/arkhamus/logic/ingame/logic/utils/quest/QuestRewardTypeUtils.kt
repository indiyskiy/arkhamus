package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUEST_REWARD_SLOTS
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty.*
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType.ADD_CLUE
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType.ITEM
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame.*
import com.arkhamusserver.arkhamus.model.ingame.InGameQuest
import com.arkhamusserver.arkhamus.model.ingame.InGameQuestReward
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class QuestRewardTypeUtils {

    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    fun chooseType(
        quest: InGameQuest,
        user: InGameUser,
        i: Int,
        previousRewards: List<InGameQuestReward>
    ): RewardType {
        val previousRewardTypes = previousRewards
            .map { it.rewardType }
            .filter { it.getOnlyOneForQuest() }
            .toSet()
        val availableByNumber = if (i != QUEST_REWARD_SLOTS - 1) {
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
        user: InGameUser
    ): Boolean {
        return byRole(type, user.role) //&& byClass(type, user.classInGame)
    }

    private fun byRole(
        type: RewardType,
        role: RoleTypeInGame
    ): Boolean {
        return when (role) {
//            CULTIST -> type in setOf(ITEM, REMOVE_CLUE)
            CULTIST -> type in setOf(ITEM)
            INVESTIGATOR -> type in setOf(ITEM, ADD_CLUE)
            NEUTRAL -> type in setOf(ITEM, ADD_CLUE)
        }
    }

//    private fun byClass(
//        type: RewardType,
//        classInGame: ClassInGame
//    ): Boolean {
//        return when (classInGame) {
//            else -> {
//                type in setOf(ITEM, ADD_CLUE, REMOVE_CLUE)
//            }
//        }
//    }

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