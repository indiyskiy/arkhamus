package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.CanAbilityBeCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualProgressRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.ritual.RitualProgressRequestMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class RitualProgressNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
    private val questProgressHandler: QuestProgressHandler,
    private val ritualHandler: RitualHandler
) : NettyRequestHandler {

    companion object{
        private val logger =LoggerFactory.getLogger(RitualProgressNettyRequestHandler::class.java)
    }

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == RitualProgressRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): RitualProgressRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as RitualProgressRequestMessage) {
            logger.info("main part")
            val inZones = zonesHandler.filterByPosition(
                requestDataHolder.nettyRequestMessage.baseRequestData.userPosition,
                globalGameData.levelGeometryData
            )
            val userId = requestDataHolder.userAccount.id
            val user = globalGameData.users[userId]!!
            val otherUsers = globalGameData.users.values.filter { it.inGameId() != userId }
            val altarHolder = globalGameData.altarHolder
            val clues = clueHandler.filterClues(
                globalGameData.clues,
                inZones,
                user
            )
            val visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents)
            val availableAbilities = canAbilityBeCastHandler.abilityOfUserResponses(user, globalGameData)
            val visibleItems = inventoryHandler.mapUsersItems(user.items)
            val ongoingCrafterProcess = crafterProcessHandler.filterAndMap(
                user,
                globalGameData.crafters,
                globalGameData.craftProcess
            )
            val userQuestProgresses = questProgressHandler.mapQuestProgresses(
                globalGameData.questProgressByUserId,
                user,
                globalGameData.quests
            )
            logger.info("event")
            val event = ongoingEvents.firstOrNull {
                it.event.type == RedisTimeEventType.RITUAL_GOING &&
                        it.event.state == RedisTimeEventState.ACTIVE
            }?.event
            logger.info("notches")
            val notches = ritualHandler.countItemsNotches(event, altarHolder)
            logger.info("current item")
            val currentItem = ritualHandler.countCurrentItem(notches, globalGameData.game.globalTimer)
            logger.info("users in ritual")
            val usersInRitual = usersInRitual(globalGameData, globalGameData.users.values)
            logger.info("build process data")
            return RitualProgressRequestProcessData(
                currentGameTime = globalGameData.game.globalTimer,
                ritualEvent = event,
                usersInRitual = usersInRitual,
                currentStepItem = currentItem,
                notches = notches,
                altarHolder = altarHolder,
                gameUser = user,
                otherGameUsers = otherUsers,
                inZones = inZones,
                visibleOngoingEvents = visibleOngoingEvents,
                availableAbilities = availableAbilities,
                visibleItems = visibleItems,
                ongoingCraftingProcess = ongoingCrafterProcess,
                containers = globalGameData.containers.values.toList(),
                crafters = globalGameData.crafters.values.toList(),
                tick = globalGameData.game.currentTick,
                clues = clues,
                userQuestProgresses = userQuestProgresses,
            )
        }
    }

    private fun usersInRitual(
        globalGameData: GlobalGameData,
        users: Collection<RedisGameUser>
    ): List<RedisGameUser> = globalGameData.altarHolder?.usersInRitual?.map { userInRitual ->
        users.first { user -> user.inGameId() == userInRitual }
    } ?: emptyList()

}
