package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestRewardUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestStepCompleteRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState.IN_PROGRESS
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.quest.QuestStepCompleteRequestMessage
import org.springframework.stereotype.Component

@Component
class QuestStepCompleteNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
    private val questProgressHandler: QuestProgressHandler,
    private val questRewardUtils: QuestRewardUtils
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == QuestStepCompleteRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): QuestStepCompleteRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as QuestStepCompleteRequestMessage) {
            val inZones = zonesHandler.filterByPosition(
                requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
                globalGameData.levelGeometryData
            )
            val userId = requestDataHolder.userAccount.id
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                inZones,
                globalGameData.castAbilities,
                userId!!
            )

            val questSteps =
                globalGameData.questProgressByUserId[userId]?.filter { it.questState == IN_PROGRESS }
            val quests = globalGameData.quests
            val questIdToStep = questSteps?.map { it.questId to it.questCurrentStep }
            val questToStep = questIdToStep
                ?.map { quests.first { quest -> quest.questId == it.first } to it.second }
                ?.map { it.first to task(it.second, it.first.levelTaskIds) }
            val quest = questToStep?.firstOrNull { it.second == this.questStepId }?.first
            val userQuestProgress = quest?.let {
                questSteps.firstOrNull { questStep -> it.questId == questStep.questId }
            }

            val questRewards = if (questRewardUtils.canBeRewarded(quest, userQuestProgress, user)) {
                val rewards = globalGameData.questRewardsByQuestId[quest?.questId]?.filter { it.userId == userId }
                questRewardUtils.findOrCreate(rewards, quest!!, userQuestProgress!!, user)
            } else {
                emptyList()
            }
            val canAccept = questProgressHandler.canAccept(quest, userQuestProgress)
            val canDecline = questProgressHandler.canDecline(quest, userQuestProgress)
            val canFinish = questProgressHandler.canFinish(quest, userQuestProgress)

            return QuestStepCompleteRequestProcessData(
                quest = quest,
                userQuestProgress = userQuestProgress,
                questRewards = questRewards,
                canAccept = canAccept,
                canDecline = canDecline,
                canFinish = canFinish,
                gameUser = user,
                otherGameUsers = users,
                inZones = inZones,
                visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents),
                availableAbilities = canAbilityBeCastHandler.abilityOfUserResponses(user, globalGameData),
                visibleItems = inventoryHandler.mapUsersItems(user.items),
                ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                    user,
                    globalGameData.crafters,
                    globalGameData.craftProcess
                ),
                containers = globalGameData.containers.values.toList(),
                crafters = globalGameData.crafters.values.toList(),
                tick = globalGameData.game.currentTick,
                clues = clues,
                userQuestProgresses = questProgressHandler.mapQuestProgresses(
                    globalGameData.questProgressByUserId,
                    user,
                    globalGameData.quests
                ),
            )
        }
    }

    private fun task(stepNumber: Int, levelTaskIds: MutableList<Long>): Long? {
        if (stepNumber <= 0) return null
        if (stepNumber >= levelTaskIds.size) return null
        return levelTaskIds[stepNumber]
    }

}



