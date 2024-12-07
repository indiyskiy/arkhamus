package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.CanAbilityBeCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.TakeQuestRewardRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.UserQuestState
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

    companion object {
        private val relevantStates = setOf(
            UserQuestState.COMPLETED,
        )
    }

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
            val users = globalGameData.users.values.filter { it.inGameId() != userId }
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                inZones,
                user
            )

            val questGiverId = this.questGiverId
            val questReward = globalGameData.questRewardsByQuestProgressId
                .values
                .firstOrNull {
                    it.any { reward ->
                        reward.id == this.questRewardId &&
                                reward.userId == userId
                    }
                }
                ?.firstOrNull { it.id == this.questRewardId }
            val quest = questReward?.questId?.let { questId ->
                globalGameData.quests.firstOrNull { it.inGameId() == questId }
            }
            val userQuestProgress = quest?.let { questNotNull ->
                globalGameData
                    .questProgressByUserId[userId]
                    ?.firstOrNull {
                        it.questId == questNotNull.inGameId() && it.questState in relevantStates
                    }
            }
            val questRewards = userQuestProgress?.let { userQuestProgressNotNull ->
                globalGameData.questRewardsByQuestProgressId[userQuestProgressNotNull.id]
            } ?: emptyList()
            val questGiverGivesReward = quest?.endQuestGiverId?.let { endQuestGiverId ->
                globalGameData.questGivers.firstOrNull { it.inGameId() == endQuestGiverId }
            }

            val rightQuestGiverForAction = questGiverId == quest?.endQuestGiverId
            val questGiver = globalGameData.questGivers.firstOrNull { it.inGameId() == this.questGiverId }

            val canAccept = questGiver != null &&
                    rightQuestGiverForAction && questProgressHandler.canAccept(quest, userQuestProgress)
            val canDecline = questGiver != null &&
                    rightQuestGiverForAction && questProgressHandler.canDecline(quest, userQuestProgress)
            val canFinish = questGiver != null &&
                    rightQuestGiverForAction && questProgressHandler.canFinish(quest, userQuestProgress)

            return TakeQuestRewardRequestProcessData(
                questReward = questReward,
                quest = quest,
                userQuestProgress = userQuestProgress,
                questRewards = questRewards,
                questGiverGivesReward = questGiverGivesReward,
                canAccept = canAccept,
                canDecline = canDecline,
                canFinish = canFinish,
                questGiver = questGiver,
                rightQuestGiverForAction = rightQuestGiverForAction,
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



