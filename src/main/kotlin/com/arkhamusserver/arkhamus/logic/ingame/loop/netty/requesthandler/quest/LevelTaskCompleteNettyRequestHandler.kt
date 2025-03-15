package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.CanAbilityBeCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestRewardUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.LevelTaskCompleteRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.quest.LevelTaskCompleteRequestMessage
import org.springframework.stereotype.Component

@Component
class LevelTaskCompleteNettyRequestHandler(
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
        nettyRequestMessage::class.java == LevelTaskCompleteRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): LevelTaskCompleteRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as LevelTaskCompleteRequestMessage) {
            val inZones = zonesHandler.filterByPosition(
                requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
                globalGameData.levelGeometryData
            )
            val userId = requestDataHolder.userAccount.id
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.inGameId() != userId }
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                user,
                globalGameData
            )

            val (quest, userQuestProgress) = questProgressHandler.questAndProgress(levelTaskId, globalGameData, userId)

            val questRewards = if (questRewardUtils.canBeRewarded(quest, userQuestProgress, user)) {
                val rewards =
                    globalGameData.questRewardsByQuestProgressId[userQuestProgress?.id]
                questRewardUtils.findOrCreate(
                    rewards,
                    quest!!,
                    userQuestProgress!!,
                    user,
                    globalGameData.game.globalTimer
                )
            } else {
                emptyList()
            }
            val canAccept = questProgressHandler.canAccept(quest, userQuestProgress)
            val canDecline = questProgressHandler.canDecline(quest, userQuestProgress)
            val canFinish = questProgressHandler.canFinish(quest, userQuestProgress)

            return LevelTaskCompleteRequestProcessData(
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
                    globalGameData,
                    globalGameData.questProgressByUserId,
                    user,
                    globalGameData.quests
                ),
            )
        }
    }

}



