package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.crafter.UpdateCrafterRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.containers.ContainerLikeThingsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.ingame.InGameCrafter
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.crafter.UpdateCrafterRequestMessage
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UpdateCrafterRequestProcessor(
    private val inGameCrafterRepository: InGameCrafterRepository,
    private val crafterTypeThingsHandler: ContainerLikeThingsHandler
) : NettyRequestProcessor {

    companion object {
        private val logger = LoggingUtils.getLogger<UpdateCrafterRequestProcessor>()
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is UpdateCrafterRequestGameData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val requestProcessData = requestDataHolder.requestProcessData as UpdateCrafterRequestGameData

        val updateCrafterRequestMessage = requestDataHolder.nettyRequestMessage as UpdateCrafterRequestMessage
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        val crafter = globalGameData.crafters[updateCrafterRequestMessage.externalInventoryId]!!

        if ((crafter.state == MapObjectState.HOLD) && (crafter.holdingUser == oldGameUser.inGameId())) {
            val sortedUserInventory =
                crafterTypeThingsHandler.getTrueNewInventoryContent(
                    crafter,
                    oldGameUser,
                    requestProcessData.sortedUserInventory
                )
            if (updateCrafterRequestMessage.close) {
                closeCrafter(crafter)
            }
            inGameCrafterRepository.save(crafter)
            requestProcessData.sortedUserInventory = sortedUserInventory
            requestProcessData.visibleItems = sortedUserInventory
        }
    }

    private fun closeCrafter(crafter: InGameCrafter) {
        crafter.holdingUser = null
        crafter.state = MapObjectState.ACTIVE
    }
}