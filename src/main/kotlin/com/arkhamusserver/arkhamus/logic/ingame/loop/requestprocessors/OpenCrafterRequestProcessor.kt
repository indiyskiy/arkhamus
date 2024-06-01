package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.OpenCrafterGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisCrafterRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.MapObjectState
import org.springframework.stereotype.Component

@Component
class OpenCrafterRequestProcessor(
    private val redisCrafterRepository: RedisCrafterRepository
) : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return request.requestProcessData is OpenCrafterGameData
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val gameData = requestDataHolder.requestProcessData as OpenCrafterGameData
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        val crafter = gameData.crafter
        if ((crafter.state == MapObjectState.ACTIVE) && (crafter.holdingUser == null)) {
            crafter.holdingUser = oldGameUser.userId
            crafter.state = MapObjectState.HOLD
            redisCrafterRepository.save(crafter)
        }
    }
}