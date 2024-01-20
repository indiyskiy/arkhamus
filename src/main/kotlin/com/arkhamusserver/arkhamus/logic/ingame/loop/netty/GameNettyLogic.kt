package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.NettyRequestHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GameNettyLogic(
    private val requestHandlers: List<NettyRequestHandler>,
    private val responseMappers: List<NettyResponseMapper>
) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameNettyLogic::class.java)
    }

    fun process(
        requestData: NettyRequestMessage,
        channel: ArkhamusChannel
    ): NettyResponseMessage {
        logger.info("process $requestData")
        val gameResponse = requestHandlers
            .first { it.acceptClass(requestData) && it.accept(requestData) }
            .process(requestData, channel.userAccount, channel.gameSession, channel)
        logger.info("game response $requestData")
        val response = responseMappers.first { it.acceptClass(gameResponse) && it.accept(gameResponse) }
            .process(gameResponse, requestData, channel.userAccount, channel.gameSession, channel.userRole)
        logger.info("netty response $response")
        return response
    }

}