package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.view.dto.netty.request.OpenContainerRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenContainerRequestProcessor(
    private val redisContainerRepository: RedisContainerRepository
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageContainer): Boolean {
        return request.nettyRequestMessage is OpenContainerRequestMessage
    }

    override fun process(
        request: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val nettyRequestMessage = request.nettyRequestMessage as OpenContainerRequestMessage
        val oldGameUser = globalGameData.users[request.userAccount.id]!!
        val container = globalGameData.containers[nettyRequestMessage.containerId]!!
        if ((container.state == MapObjectState.ACTIVE) && (container.holdingUser == null)) {
            container.holdingUser = oldGameUser.userId
            container.state = MapObjectState.HOLD
            redisContainerRepository.save(container)
        }

    }
}