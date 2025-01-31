package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameQuestRewardRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType.ADD_CLUE
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType.ITEM
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.UserQuestState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.ingame.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.QuestRewardResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class QuestRewardUtils(
    private val questRewardRepository: InGameQuestRewardRepository,
    private val questRewardTypeUtils: QuestRewardTypeUtils,
    private val questRewardItemUtils: QuestRewardItemUtils,
    private val questRewardAmountUtils: QuestRewardAmountUtils,
    private val inventoryHandler: InventoryHandler,
    private val clueHandler: ClueHandler
) {
    companion object {
        private var logger: Logger = LoggerFactory.getLogger(QuestRewardUtils::class.java)
        private val random = Random(System.currentTimeMillis())
    }

    fun mapRewards(questRewards: List<InGameQuestReward>): List<QuestRewardResponse> {
        return questRewards.map {
            QuestRewardResponse(
                rewardId = it.id,
                rewardType = it.rewardType,
                rewardItem = it.rewardItem?.id,
                rewardAmount = it.rewardAmount,
            )
        }
    }

    fun canBeRewarded(
        quest: InGameQuest?,
        userQuestProgress: InGameUserQuestProgress?,
        user: InGameGameUser
    ): Boolean {
        return quest != null &&
                userQuestProgress != null &&
                userQuestProgress.questId == quest.inGameId() &&
                userQuestProgress.userId == user.inGameId() &&
                userQuestProgress.questState !in setOf(UserQuestState.FINISHED, UserQuestState.DECLINED)
    }

    @Transactional
    fun findOrCreate(
        rewards: List<InGameQuestReward>?,
        quest: InGameQuest,
        questProgress: InGameUserQuestProgress,
        user: InGameGameUser,
        currentGameTime: Long
    ): List<InGameQuestReward> {
        return if (rewards.isNullOrEmpty()) {
            generateQuestRewardsForUser(
                quest,
                questProgress,
                user,
                currentGameTime,
                emptyList()
            )
        } else {
            val rewardsOfUser = rewards.filter { it.userId == user.inGameId() }
            val filtered = rewardsOfUser.filter { it.questId == quest.inGameId() }
            filtered.ifEmpty {
                generateQuestRewardsForUser(
                    quest,
                    questProgress,
                    user,
                    currentGameTime,
                    rewardsOfUser
                )
            }
        }
    }

    private fun generateQuestRewardsForUser(
        quest: InGameQuest,
        questProgress: InGameUserQuestProgress,
        user: InGameGameUser,
        currentGameTime: Long,
        allRewardsOfUser: List<InGameQuestReward>
    ): List<InGameQuestReward> {
        logger.info("generating rewards for: ${quest.difficulty} ${quest.inGameId()}")
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
                questProgress = questProgress,
                user = user,
                i = 0,
                previousRewards = emptyList(),
                currentGameTime = currentGameTime,
                rewardsFromPreviousQuest = allItemsOfLastQuestRewards
            )
        val second =
            generateQuestRewardsForUser(
                quest = quest,
                questProgress = questProgress,
                user = user,
                i = 1,
                previousRewards = listOf(first),
                currentGameTime = currentGameTime,
                rewardsFromPreviousQuest = allItemsOfLastQuestRewards
            )
        val third = generateQuestRewardsForUser(
            quest = quest,
            questProgress = questProgress,
            user = user,
            i = 2,
            previousRewards = listOf(first, second),
            currentGameTime = currentGameTime,
            rewardsFromPreviousQuest = allItemsOfLastQuestRewards
        )
        val forth = generateQuestRewardsForUser(
            quest = quest,
            questProgress = questProgress,
            user = user,
            i = 3,
            previousRewards = listOf(first, second, third),
            currentGameTime = currentGameTime,
            rewardsFromPreviousQuest = allItemsOfLastQuestRewards
        )

        val rewards = listOf(first, second, third, forth)

        rewards.forEach {
            logger.info("generated reward: ${it.rewardType} ${it.rewardItem} ${it.rewardAmount}")
        }
        questRewardRepository.saveAll(rewards)
        return rewards
    }

    private fun generateQuestRewardsForUser(
        quest: InGameQuest,
        questProgress: InGameUserQuestProgress,
        user: InGameGameUser,
        i: Int,
        previousRewards: List<InGameQuestReward>,
        currentGameTime: Long,
        rewardsFromPreviousQuest: List<Item>
    ): InGameQuestReward {
        val rewardType = questRewardTypeUtils.chooseType(quest, user, i, previousRewards)
        val rewardItem =
            questRewardItemUtils.chooseItem(quest, user, rewardType, previousRewards, rewardsFromPreviousQuest)
        val rewardAmount = questRewardAmountUtils.chooseAmount(quest, user, rewardType, rewardItem)
        return InGameQuestReward(
            id = generateRandomId(),
            rewardType = rewardType,
            rewardAmount = rewardAmount,
            rewardItem = rewardItem,
            gameId = quest.gameId,
            questId = quest.inGameId(),
            questProgressId = questProgress.id,
            userId = user.inGameId(),
            creationGameTime = currentGameTime,
        )
    }

    fun takeReward(
        user: InGameGameUser,
        reward: InGameQuestReward,
        globalGameData: GlobalGameData,
        questGiverGivesReward: InGameQuestGiver
    ) {
        val tags = questGiverGivesReward.gameTags()
        when (reward.rewardType) {
            ITEM -> {
                if (tags.contains(InGameObjectTag.DARK_THOUGHTS)) {
                    takeCorruptedItems(reward, user)
                } else {
                    takeItems(reward, user)
                }
            }

            ADD_CLUE -> {
                if (!tags.contains(InGameObjectTag.DARK_THOUGHTS)) {
                    clueHandler.addRandomClue(globalGameData, user, true)
                } else {
                    clueHandler.removeRandomClue(globalGameData)
                }
            }

        }
    }

    private fun takeItems(
        reward: InGameQuestReward,
        user: InGameGameUser
    ) {
        if (reward.rewardItem != null) {
            inventoryHandler.addItems(user, reward.rewardItem!!, reward.rewardAmount)
        }
    }

    private fun takeCorruptedItems(
        reward: InGameQuestReward,
        user: InGameGameUser
    ) {
        inventoryHandler.addItems(
            user, Item.values().filter {
                it.itemType == ItemType.CULTIST_LOOT
            }.random(random),
            reward.rewardAmount
        )
    }
}