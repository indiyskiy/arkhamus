package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.toItem
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.toItemName
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisQuestRewardRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType.*
import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisQuestReward
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.QuestRewardResponse
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class QuestRewardUtils(
    private val questRewardRepository: RedisQuestRewardRepository,
    private val questRewardTypeUtils: QuestRewardTypeUtils,
    private val questRewardItemUtils: QuestRewardItemUtils,
    private val questRewardAmountUtils: QuestRewardAmountUtils,
    private val inventoryHandler: InventoryHandler
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(QuestRewardUtils::class.java)
    }

    fun mapRewards(questRewards: List<RedisQuestReward>): List<QuestRewardResponse> {
        return questRewards.map {
            QuestRewardResponse(
                rewardId = it.id,
                rewardType = it.rewardType,
                rewardItem = it.rewardItem,
                rewardAmount = it.rewardAmount,
            )
        }
    }

    fun canBeRewarded(
        quest: RedisQuest?,
        userQuestProgress: RedisUserQuestProgress?,
        user: RedisGameUser
    ): Boolean {
        return quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questId == quest.questId &&
                userQuestProgress.userId == user.userId &&
                userQuestProgress.questState !in setOf(UserQuestState.FINISHED, UserQuestState.DECLINED)
    }

    fun findOrCreate(
        rewards: List<RedisQuestReward>?,
        quest: RedisQuest,
        userQuestProgress: RedisUserQuestProgress,
        user: RedisGameUser
    ): List<RedisQuestReward> {
        return if (rewards.isNullOrEmpty()) {
            generateQuestRewardsForUser(quest, user)
        } else {
            val filtered = rewards.filter { it.questId == quest.questId && it.userId == user.userId }
            filtered.ifEmpty {
                generateQuestRewardsForUser(quest, user)
            }
        }
    }


    private fun generateQuestRewardsForUser(
        quest: RedisQuest,
        user: RedisGameUser
    ): List<RedisQuestReward> {
        logger.info("generating rewards for: ${quest.difficulty} ${quest.questId}")
        val rewards = (0..2).map { i ->
            generateQuestRewardsForUser(quest, user, i)
        }
        rewards.forEach {
            logger.info("generated reward: ${it.rewardType} ${it.rewardItem.toItemName()} ${it.rewardAmount}")
        }
        questRewardRepository.saveAll(rewards)
        return rewards
    }

    private fun generateQuestRewardsForUser(quest: RedisQuest, user: RedisGameUser, i: Int): RedisQuestReward {
        val rewardType = questRewardTypeUtils.chooseType(quest, user, i)
        val rewardItem = questRewardItemUtils.chooseItem(quest, user, rewardType, i)
        val rewardAmount = questRewardAmountUtils.chooseAmount(quest, user, rewardType, rewardItem, i)
        return RedisQuestReward(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            rewardType = rewardType,
            rewardAmount = rewardAmount,
            rewardItem = rewardItem?.id,
            gameId = quest.gameId,
            questId = quest.questId,
            userId = user.userId,
        )
    }

    fun takeReward(
        user: RedisGameUser,
        quest: RedisQuest,
        reward: RedisQuestReward
    ): Boolean {
        when (reward.rewardType) {
            ITEM -> {
                takeItems(reward, user)
                return true
            }

            ADD_CLUE -> {
                return true
            }
        }
    }

    private fun takeItems(
        reward: RedisQuestReward,
        user: RedisGameUser
    ) {
        inventoryHandler.addItems(user, reward.rewardItem!!.toItem(), reward.rewardAmount)
    }
}