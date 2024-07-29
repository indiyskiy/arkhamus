package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestRewardUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestAcceptRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.quest.QuestAcceptRequestMessage
import org.springframework.stereotype.Component

@Component
class QuestAcceptNettyRequestHandler(
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
        nettyRequestMessage::class.java == QuestAcceptRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): QuestAcceptRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as QuestAcceptRequestMessage) {
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
            val userQuestProgresses = globalGameData.questProgressByUserId[userId]
            val quest = globalGameData.quests.firstOrNull {
                it.questId == questId
            }
            val userQuestProgress =
                quest?.let { questNotNull -> userQuestProgresses?.firstOrNull { it.questId == questNotNull.questId } }
            val questRewards = if (questRewardUtils.canBeRewarded(quest, userQuestProgress, user)) {
                val rewards = globalGameData.questRewardsByQuestId[quest?.questId]?.filter { it.userId == userId }
                questRewardUtils.findOrCreate(rewards, quest!!, userQuestProgress!!, user)
            } else {
                emptyList()
            }
            val canAccept = questProgressHandler.canAccept(quest, userQuestProgress)
            val canDecline = questProgressHandler.canDecline(quest, userQuestProgress)
            val canFinish = questProgressHandler.canFinish(quest, userQuestProgress)

            return QuestAcceptRequestProcessData(
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

}



