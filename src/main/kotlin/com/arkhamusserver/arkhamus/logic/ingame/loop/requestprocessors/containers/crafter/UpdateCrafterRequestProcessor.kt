package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.containers.crafter

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.UpdateCrafterRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.ContainerLikeThingsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.crafter.UpdateCrafterRequestMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UpdateCrafterRequestProcessor(
    private val redisCrafterRepository: RedisCrafterRepository,
    private val crafterTypeThingsHandler: ContainerLikeThingsHandler
) : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(UpdateCrafterRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is UpdateCrafterRequestGameData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val requestProcessData = requestDataHolder.requestProcessData as UpdateCrafterRequestGameData

        val updateCrafterRequestMessage = requestDataHolder.nettyRequestMessage as UpdateCrafterRequestMessage
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        val crafter = globalGameData.crafters[updateCrafterRequestMessage.externalInventoryId]!!

        if ((crafter.state == MapObjectState.HOLD) && (crafter.holdingUser == oldGameUser.userId)) {
            val sortedUserInventory =
                crafterTypeThingsHandler.getTrueNewInventoryContent(
                    crafter,
                    oldGameUser,
                    requestProcessData.sortedUserInventory
                )
            if (updateCrafterRequestMessage.close) {
                closeCrafter(crafter)
            }
            redisCrafterRepository.save(crafter)
            requestProcessData.sortedUserInventory = sortedUserInventory
            requestProcessData.visibleItems = sortedUserInventory
        }
    }

    private fun closeCrafter(crafter: RedisCrafter) {
        crafter.holdingUser = null
        crafter.state = MapObjectState.ACTIVE
    }
}