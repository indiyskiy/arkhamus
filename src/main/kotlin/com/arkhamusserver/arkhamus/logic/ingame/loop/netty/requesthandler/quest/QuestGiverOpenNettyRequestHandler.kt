package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestRewardUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.QuestGiverOpenRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState
import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState.*
import com.arkhamusserver.arkhamus.model.redis.RedisQuest
import com.arkhamusserver.arkhamus.model.redis.RedisUserQuestProgress
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.quest.QuestGiverOpenRequestMessage
import org.springframework.stereotype.Component

@Component
class QuestGiverOpenNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
    private val questProgressHandler: QuestProgressHandler,
    private val questRewardUtils: QuestRewardUtils
) : NettyRequestHandler {

    companion object {
        private val reasonableStates = setOf(
            AWAITING,
            READ,
            DECLINED,
            IN_PROGRESS,
            COMPLETED,
        )
    }

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == QuestGiverOpenRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): QuestGiverOpenRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as QuestGiverOpenRequestMessage) {
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

            val userQuestProgresses =
                globalGameData.questProgressByUserId[userId]?.filter {
                    it.questState in reasonableStates
                }
            val userQuestIds = userQuestProgresses?.map {
                it.questId
            }?.toSet() ?: emptySet()
            val questsOptions = globalGameData.quests.filter {
                it.questId in userQuestIds
            }

            val questOptionIds = questsOptions.map { it.questId }.toSet()
            val userQuestProgressOptions =
                userQuestProgresses?.filter {
                    it.questId in questOptionIds && rightNpcForState(
                        it.questId,
                        questsOptions,
                        it.questState,
                        this.questGiverId
                    )
                }

            val userQuestProgress = userQuestProgress(userQuestProgressOptions)

            val quest =
                userQuestProgress?.let { progress -> questsOptions.firstOrNull { progress.questId == it.questId } }

            val questRewards = if (questRewardUtils.canBeRewarded(quest, userQuestProgress, user)) {
                val rewards = globalGameData.questRewardsByQuestId[quest?.questId]?.filter { it.userId == userId }
                questRewardUtils.findOrCreate(rewards, quest!!, user)
            } else {
                emptyList()
            }


            val canAccept = this.questGiverId == quest?.startQuestGiverId &&
                    questProgressHandler.canAccept(quest, userQuestProgress)
            val canDecline = this.questGiverId == quest?.startQuestGiverId &&
                    questProgressHandler.canDecline(quest, userQuestProgress)
            val canFinish = this.questGiverId == quest?.endQuestGiverId &&
                    questProgressHandler.canFinish(quest, userQuestProgress)

            return QuestGiverOpenRequestProcessData(
                quest = quest,
                userQuestProgress = userQuestProgress,
                questRewards = questRewards,
                canAccept = canAccept,
                canDecline = canDecline,
                canFinish = canFinish,
                questGiverId = this.questGiverId,
                rightQuestGiverForAction = canAccept || canDecline || canFinish,
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

    private fun rightNpcForState(
        questId: Long,
        questsOptions: List<RedisQuest>,
        questState: UserQuestState,
        questGiverId: Long
    ): Boolean {
        val quest = questsOptions.firstOrNull { it.questId == questId }
        return quest?.let {
            when (questState) {
                AWAITING,
                READ,
                DECLINED,
                IN_PROGRESS -> questGiverId == quest.startQuestGiverId

                COMPLETED -> questGiverId == quest.endQuestGiverId

                FINISHED,
                FINISHED_AVAILABLE,
                DECLINED_AVAILABLE -> false
            }
        } ?: false

    }

    private fun userQuestProgress(userQuestProgressOptions: List<RedisUserQuestProgress>?): RedisUserQuestProgress? =
        ((userQuestProgressOptions?.firstOrNull { it.questState == COMPLETED })
            ?: (userQuestProgressOptions?.firstOrNull { it.questState == AWAITING })
            ?: (userQuestProgressOptions?.firstOrNull()))

}



