package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.springframework.stereotype.Component

@Component
class NettyResponseBuilder(
    private val responseMapper: List<NettyResponseMapper>,
) {
    fun buildResponse(
        response: GameData,
        requestContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData
    ): NettyResponseMessage {
        return responseMapper.first {
            it.acceptClass(response) && it.accept(response)
        }.process(
            response,
            requestContainer.nettyRequestMessage,
            requestContainer.userAccount,
            requestContainer.gameSession,
            requestContainer.userRole,
        )
    }

}
