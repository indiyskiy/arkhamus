package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container.UpdateContainerRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.containers.ContainerLikeThingsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.banvote.VoteSpotCastRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameContainerRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.netty.request.containers.container.UpdateContainerRequestMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UpdateContainerRequestProcessor(
    private val inGameContainerRepository: InGameContainerRepository,
    private val containerLikeThingsHandler: ContainerLikeThingsHandler
) : NettyRequestProcessor {

    companion object {
        private val logger = LoggingUtils.getLogger<UpdateContainerRequestProcessor>()
    }

    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is UpdateContainerRequestGameData
    }

    @Transactional
    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val requestProcessData = requestDataHolder.requestProcessData as UpdateContainerRequestGameData

        val updateContainerRequestMessage = requestDataHolder.nettyRequestMessage as UpdateContainerRequestMessage
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        val container = globalGameData.containers[updateContainerRequestMessage.externalInventoryId]!!

        logger.info("start update container")
        if ((container.state == MapObjectState.HOLD) && (container.holdingUser == oldGameUser.inGameId())) {
            val sortedUserInventory =
                containerLikeThingsHandler.getTrueNewInventoryContent(
                    container,
                    oldGameUser,
                    requestProcessData.sortedUserInventory
                )
            if (updateContainerRequestMessage.close) {
                logger.info("close container")
                closeContainer(container)
            }
            logger.info("save container")
            inGameContainerRepository.save(container)
            logger.info("start update container")
            requestProcessData.sortedUserInventory = sortedUserInventory
            requestProcessData.visibleItems = sortedUserInventory
        }
    }


    private fun closeContainer(container: InGameContainer) {
        container.holdingUser = null
        container.state = MapObjectState.ACTIVE
    }
}