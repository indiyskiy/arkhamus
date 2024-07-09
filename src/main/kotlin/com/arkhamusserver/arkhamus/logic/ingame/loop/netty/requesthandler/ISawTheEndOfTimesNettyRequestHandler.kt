package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CanAbilityBeCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ErrorGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ISawTheEndOfTimesRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.view.dto.netty.request.ISawTheEndOfTimesRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class ISawTheEndOfTimesNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == ISawTheEndOfTimesRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): RequestProcessData {
        val userId = requestDataHolder.userAccount.id
        requestDataHolder.gameSession?.id?.let {
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            val visibleOngoingEvents = eventVisibilityFilter.filter(user, ongoingEvents)
            val gameEnded = globalGameData.game.state in setOf(GameState.GAME_END_SCREEN.name, GameState.FINISHED.name)

            return ISawTheEndOfTimesRequestGameData(
                gameEnded = gameEnded,
                gameUser = user,
                otherGameUsers = users,
                visibleOngoingEvents = visibleOngoingEvents,
                availableAbilities = canAbilityBeCastHandler.abilityOfUserResponses(user, globalGameData),
                visibleItems = inventoryHandler.mapUsersItems(user.items),
                tick = globalGameData.game.currentTick,
                ongoingCraftingProcess = crafterProcessHandler.filterAndMap(
                    user = user,
                    crafters = globalGameData.crafters,
                    globalGameData.craftProcess
                ),
                containers = globalGameData.containers.values.toList(),
            )
        } ?: return ErrorGameResponse("game session id is null", globalGameData.game.currentTick)
    }

}