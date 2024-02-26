package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.EventVisibilityFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ErrorGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.HeartbeatGameData
import com.arkhamusserver.arkhamus.view.dto.netty.request.HeartbeatRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import org.springframework.stereotype.Component

@Component
class HeartbeatNettyRequestHandler(
    private val eventVisibilityFilter: EventVisibilityFilter
) : NettyRequestHandler {

    override fun acceptClass(nettyRequestMessage: NettyBaseRequestMessage): Boolean =
        nettyRequestMessage::class.java == HeartbeatRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyBaseRequestMessage): Boolean = true

    override fun buildData(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEffects: List<OngoingEvent>
    ): GameData {
        val userId = nettyTickRequestMessageContainer.userAccount.id
            nettyTickRequestMessageContainer.gameSession?.id?.let {
                val user = globalGameData.users[userId]!!
                val users = globalGameData.users.values.filter { it.userId != userId }
                val visibleOngoingEffects = eventVisibilityFilter.filter(user, ongoingEffects)
                return HeartbeatGameData(
                    user,
                    users,
                    visibleOngoingEffects,
                    globalGameData.game.currentTick
                )
            } ?: return ErrorGameResponse("game session id is null", globalGameData.game.currentTick)
    }

}