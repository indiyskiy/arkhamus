package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.toItem
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.toItemName
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisQuestRewardRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType.*
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.UserQuestState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisQuestReward
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.QuestRewardResponse
import com.fasterxml.uuid.Generators
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class QuestRewardUtils(
    private val questRewardRepository: RedisQuestRewardRepository,
    private val questRewardTypeUtils: QuestRewardTypeUtils,
    private val questRewardItemUtils: QuestRewardItemUtils,
    private val questRewardAmountUtils: QuestRewardAmountUtils,
    private val inventoryHandler: InventoryHandler,
    private val clueHandler: ClueHandler
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

    @Transactional
    fun findOrCreate(
        rewards: List<RedisQuestReward>?,
        quest: RedisQuest,
        user: RedisGameUser,
        currentGameTime: Long
    ): List<RedisQuestReward> {
        return if (rewards.isNullOrEmpty()) {
            generateQuestRewardsForUser(quest, user, currentGameTime, emptyList())
        } else {
            val rewardsOfUser = rewards.filter { it.userId == user.userId }
            val filtered = rewardsOfUser.filter { it.questId == quest.questId }
            filtered.ifEmpty {
                generateQuestRewardsForUser(quest, user, currentGameTime, rewardsOfUser)
            }
        }
    }

    private fun generateQuestRewardsForUser(
        quest: RedisQuest,
        user: RedisGameUser,
        currentGameTime: Long,
        allRewardsOfUser: List<RedisQuestReward>
    ): List<RedisQuestReward> {
        logger.info("generating rewards for: ${quest.difficulty} ${quest.questId}")
        val lastReward = allRewardsOfUser
            .filter {
                it.rewardType == ITEM
            }
            .maxByOrNull {
                it.creationGameTime
            }
        val allRewardsOfLastQuest = allRewardsOfUser.filter { it.questId == lastReward?.questId }
        val allItemsOfLastQuestRewards =
            allRewardsOfLastQuest.filter { it.rewardType == ITEM }.mapNotNull { it.rewardItem }

        val first =
            generateQuestRewardsForUser(
                quest = quest,
                user = user,
                i = 0,
                previousRewards = emptyList(),
                currentGameTime = currentGameTime,
                rewardsFromPreviousQuest = allItemsOfLastQuestRewards
            )
        val second =
            generateQuestRewardsForUser(
                quest = quest,
                user = user,
                i = 1,
                previousRewards = listOf(first),
                currentGameTime = currentGameTime,
                rewardsFromPreviousQuest = allItemsOfLastQuestRewards
            )
        val third = generateQuestRewardsForUser(
            quest = quest,
            user = user,
            i = 2,
            previousRewards = listOf(first, second),
            currentGameTime = currentGameTime,
            rewardsFromPreviousQuest = allItemsOfLastQuestRewards
        )
        val forth = generateQuestRewardsForUser(
            quest = quest,
            user = user,
            i = 3,
            previousRewards = listOf(first, second, third),
            currentGameTime = currentGameTime,
            rewardsFromPreviousQuest = allItemsOfLastQuestRewards
        )

        val rewards = listOf(first, second, third, forth)

        rewards.forEach {
            logger.info("generated reward: ${it.rewardType} ${it.rewardItem.toItemName()} ${it.rewardAmount}")
        }
        questRewardRepository.saveAll(rewards)
        return rewards
    }

    private fun generateQuestRewardsForUser(
        quest: RedisQuest,
        user: RedisGameUser,
        i: Int,
        previousRewards: List<RedisQuestReward>,
        currentGameTime: Long,
        rewardsFromPreviousQuest: List<Int>
    ): RedisQuestReward {
        val rewardType = questRewardTypeUtils.chooseType(quest, user, i, previousRewards)
        val rewardItem =
            questRewardItemUtils.chooseItem(quest, user, rewardType, previousRewards, rewardsFromPreviousQuest)
        val rewardAmount = questRewardAmountUtils.chooseAmount(quest, user, rewardType, rewardItem)
        return RedisQuestReward(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            rewardType = rewardType,
            rewardAmount = rewardAmount,
            rewardItem = rewardItem?.id,
            gameId = quest.gameId,
            questId = quest.questId,
            userId = user.userId,
            creationGameTime = currentGameTime,
        )
    }

    fun takeReward(
        user: RedisGameUser,
        reward: RedisQuestReward,
        globalGameData: GlobalGameData
    ) {
        when (reward.rewardType) {
            ITEM -> {
                takeItems(reward, user)
            }

            ADD_CLUE -> {
                clueHandler.addRandomClue(globalGameData)
            }

            REMOVE_CLUE -> {
                clueHandler.removeRandomClue(globalGameData)
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