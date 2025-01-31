package com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import org.springframework.stereotype.Component

@Component
class BaseRequestProcessor : NettyRequestProcessor {

    companion object {
        private val RESTRICTION_SET = setOf(
            UserStateTag.IN_RITUAL,
            UserStateTag.STUN,
        )
    }

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
        if (oldGameUser.notRestricted()) {
            oldGameUser.x = nettyRequestMessage.baseRequestData.userPosition.x
            oldGameUser.y = nettyRequestMessage.baseRequestData.userPosition.y
            oldGameUser.z = nettyRequestMessage.baseRequestData.userPosition.z
        }
    }

    private fun InGameGameUser.notRestricted(): Boolean {
        return !restricted()
    }

    private fun InGameGameUser.restricted(): Boolean {
        return RESTRICTION_SET.any { stateTags.contains(it) }
    }
}
