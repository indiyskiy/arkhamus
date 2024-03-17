package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ContainerGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameData
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.OpenContainerRequestMessage
import org.springframework.stereotype.Component

@Component
class OpenContainerNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == OpenContainerRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEffects: List<OngoingEvent>
    ): GameData {
        val userId = nettyTickRequestMessageContainer.userAccount.id
        val request = nettyTickRequestMessageContainer.nettyRequestMessage
        with(request as OpenContainerRequestMessage) {
            val container = globalGameData.containers[this.containerId]!!
            val user = globalGameData.users[userId]!!
            val users = globalGameData.users.values.filter { it.userId != userId }
            return ContainerGameData(
                container,
                user,
                users,
                eventVisibilityFilter.filter(user, ongoingEffects),
                globalGameData.game.currentTick
            )
        }
    }
}