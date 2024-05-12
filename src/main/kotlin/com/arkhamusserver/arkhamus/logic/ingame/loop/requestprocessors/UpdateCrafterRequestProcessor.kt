package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.UpdateCrafterGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.ContainerTypeThingsHandler
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.view.dto.netty.request.UpdateCrafterRequestMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UpdateCrafterRequestProcessor(
    private val redisCrafterRepository: RedisCrafterRepository,
    private val containerTypeThingsHandler: ContainerTypeThingsHandler
) : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(UpdateCrafterRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.nettyRequestMessage is UpdateCrafterRequestMessage
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val requestProcessData = requestDataHolder.requestProcessData as UpdateCrafterGameData

        val updateCrafterRequestMessage = requestDataHolder.nettyRequestMessage as UpdateCrafterRequestMessage
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        val crafter = globalGameData.crafters[updateCrafterRequestMessage.crafterId]!!

        if ((crafter.state == MapObjectState.HOLD) && (crafter.holdingUser == oldGameUser.userId)) {
            val sortedInventory =
               containerTypeThingsHandler.getTrueNewInventoryContent(crafter, oldGameUser, updateCrafterRequestMessage.newInventoryContent)
            closeCrafter(crafter)
            requestProcessData.sortedInventory = sortedInventory
            requestProcessData.visibleItems = sortedInventory
        }
    }

    private fun closeCrafter(crafter: RedisCrafter) {
        crafter.holdingUser = null
        crafter.state = MapObjectState.ACTIVE
        redisCrafterRepository.save(crafter)
    }
}