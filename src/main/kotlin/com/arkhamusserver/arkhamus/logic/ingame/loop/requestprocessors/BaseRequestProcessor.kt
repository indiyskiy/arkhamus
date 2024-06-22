package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.enums.ingame.UserStateTag
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class BaseRequestProcessor : NettyRequestProcessor {
    override fun accept(request: NettyTickRequestMessageDataHolder): Boolean {
        return true
    }

    override fun process(
        requestDataHolder: NettyTickRequestMessageDataHolder,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val nettyRequestMessage = requestDataHolder.nettyRequestMessage
        val oldGameUser = globalGameData.users[requestDataHolder.userAccount.id]!!
        if (oldGameUser.notRestricted(globalGameData)) {
            oldGameUser.x = nettyRequestMessage.baseRequestData.userPosition.x
            oldGameUser.y = nettyRequestMessage.baseRequestData.userPosition.y
        }
    }

    private fun RedisGameUser.notRestricted(globalGameData: GlobalGameData): Boolean {
        return !restricted(globalGameData)
    }

    private fun RedisGameUser.restricted(globalGameData: GlobalGameData): Boolean {
        return stateTags.contains(UserStateTag.IN_RITUAL.name)
    }
}
