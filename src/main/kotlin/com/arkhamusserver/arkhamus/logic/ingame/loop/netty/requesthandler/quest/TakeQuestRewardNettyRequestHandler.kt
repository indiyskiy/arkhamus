package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.TakeQuestRewardRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.quest.TakeQuestRewardRequestMessage
import org.springframework.stereotype.Component

@Component
class TakeQuestRewardNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
    private val questProgressHandler: QuestProgressHandler,
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == TakeQuestRewardRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): TakeQuestRewardRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as TakeQuestRewardRequestMessage) {
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

            val questReward = globalGameData.questRewardsByQuestId
                .values
                .firstOrNull {
                    it.any { reward ->
                        reward.id == this.questRewardId &&
                                reward.userId == userId
                    }
                }
                ?.firstOrNull { it.id == this.questRewardId }
            val quest = questReward?.questId?.let { questId ->
                globalGameData.quests.firstOrNull { it.questId == questId }
            }
            val userQuestProgress = quest?.let { questNotNull ->
                globalGameData
                    .questProgressByUserId[userId]
                    ?.firstOrNull {
                        it.questId == questNotNull.questId
                    }
            }
            val questRewards = quest?.let { questNotNull ->
                globalGameData.questRewardsByQuestId[questNotNull.questId]
            } ?: emptyList()

            val canAccept = questProgressHandler.canAccept(quest, userQuestProgress)
            val canDecline = questProgressHandler.canDecline(quest, userQuestProgress)
            val canFinish = questProgressHandler.canFinish(quest, userQuestProgress)

            return TakeQuestRewardRequestProcessData(
                questReward = questReward,
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



