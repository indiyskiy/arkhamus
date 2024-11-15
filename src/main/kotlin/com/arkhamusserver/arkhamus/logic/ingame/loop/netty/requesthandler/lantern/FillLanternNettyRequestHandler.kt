package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.lantern

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ClueHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.InventoryHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.LanternHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ability.CanAbilityBeCastHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.craft.CrafterProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.lantern.FillLanternRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.lantern.FillLanternRequestMessage
import org.springframework.stereotype.Component

@Component
class FillLanternNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter,
    private val canAbilityBeCastHandler: CanAbilityBeCastHandler,
    private val inventoryHandler: InventoryHandler,
    private val crafterProcessHandler: CrafterProcessHandler,
    private val zonesHandler: ZonesHandler,
    private val clueHandler: ClueHandler,
    private val questProgressHandler: QuestProgressHandler,
    private val lanternHandler: LanternHandler
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == FillLanternRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): FillLanternRequestProcessData {
        val request = requestDataHolder.nettyRequestMessage
        with(request as FillLanternRequestMessage) {
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
                user
            )

            val lantern = globalGameData.lanterns.firstOrNull { it.inGameId() == this.lanternId }

            val canFill = lanternHandler.canFill(user, lantern)

            return FillLanternRequestProcessData(
                lantern = lantern,
                canFill = canFill,
                successfullyFilled = false,
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



