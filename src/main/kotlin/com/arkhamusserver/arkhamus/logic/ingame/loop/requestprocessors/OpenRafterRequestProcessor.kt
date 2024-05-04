package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import com.arkhamusserver.arkhamus.view.dto.netty.request.OpenCrafterRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenRafterRequestProcessor(
    private val redisCrafterRepository: RedisCrafterRepository
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.nettyRequestMessage is OpenCrafterRequestMessage
    }

    override fun process(
        requestCrafter: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val nettyRequestMessage = requestCrafter.nettyRequestMessage as OpenCrafterRequestMessage
        val oldGameUser = globalGameData.users[requestCrafter.userAccount.id]!!
        val crafter = globalGameData.crafters[nettyRequestMessage.crafterId]!!
        if ((crafter.state == MapObjectState.ACTIVE) && (crafter.holdingUser == null)) {
            crafter.holdingUser = oldGameUser.userId
            crafter.state = MapObjectState.HOLD
            redisCrafterRepository.save(crafter)
        }
    }
}