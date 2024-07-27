package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.toItemName
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisQuestRewardRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisQuestReward
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
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(QuestRewardUtils::class.java)
    }

    fun generateQuestRewardsForUser(
        rewards: List<RedisQuestReward>,
        quest: RedisQuest,
        user: RedisGameUser
    ): List<RedisQuestReward> {
        val oldRewards = rewards.filter { it.userId == user.userId }
        return if (oldRewards.isEmpty()) {
            generateQuestRewardsForUser(quest, user)
        } else {
            oldRewards
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

}