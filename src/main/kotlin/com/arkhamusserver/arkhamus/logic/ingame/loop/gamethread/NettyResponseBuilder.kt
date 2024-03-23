package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.springframework.stereotype.Component

@Component
class NettyResponseBuilder(
    private val responseMapper: List<NettyResponseMapper>,
) {
    fun buildResponse(
        requestContainer: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData
    ): NettyResponseMessage {
        return responseMapper.first {
            it.acceptClass(requestContainer.requestProcessData!!) && it.accept(requestContainer.requestProcessData!!)
        }.process(
            requestContainer.requestProcessData!!,
            requestContainer.nettyRequestMessage,
            requestContainer.userAccount,
            requestContainer.gameSession,
            requestContainer.userRole,
        )
    }

}
