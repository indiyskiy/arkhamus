package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.UpdateContainerGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.ContainerLikeThingsHandler
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.view.dto.netty.request.UpdateContainerRequestMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UpdateContainerRequestProcessor(
    private val redisContainerRepository: RedisContainerRepository,
    private val containerLikeThingsHandler: ContainerLikeThingsHandler
) : NettyRequestProcessor {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(UpdateContainerRequestProcessor::class.java)
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.nettyRequestMessage is UpdateContainerRequestMessage
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val requestProcessData = requestDataHolder.requestProcessData as UpdateContainerGameData

        val updateContainerRequestMessage = requestDataHolder.nettyRequestMessage as UpdateContainerRequestMessage
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        val container = globalGameData.containers[updateContainerRequestMessage.externalInventoryId]!!

        if ((container.state == MapObjectState.HOLD) && (container.holdingUser == oldGameUser.userId)) {
            val sortedUserInventory =
                containerLikeThingsHandler.getTrueNewInventoryContent(
                    container,
                    oldGameUser,
                    requestProcessData.sortedUserInventory
                )
            if (updateContainerRequestMessage.close) {
                closeContainer(container)
            }
            redisContainerRepository.save(container)
            requestProcessData.sortedUserInventory = sortedUserInventory
            requestProcessData.visibleItems = sortedUserInventory
        }
    }


    private fun closeContainer(container: RedisContainer) {
        container.holdingUser = null
        container.state = MapObjectState.ACTIVE
    }
}