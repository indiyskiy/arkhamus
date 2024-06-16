package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.OpenContainerRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisContainerRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import org.springframework.stereotype.Component

@Component
class OpenContainerRequestProcessor(
    private val redisContainerRepository: RedisContainerRepository
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is OpenContainerRequestGameData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val gameData = requestDataHolder.requestProcessData as OpenContainerRequestGameData
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        val container = gameData.container
        if ((container.state == MapObjectState.ACTIVE) && (container.holdingUser == null)) {
            container.holdingUser = oldGameUser.userId
            container.state = MapObjectState.HOLD
            redisContainerRepository.save(container)
        }
    }
}