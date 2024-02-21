package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AuthGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.AuthNettyRequestHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.AuthNettyResponseMapper
import com.arkhamusserver.arkhamus.model.enums.AuthState
import com.arkhamusserver.arkhamus.view.dto.netty.request.AuthRequestMessage
import  com.arkhamusserver.arkhamus.globalutils.toJson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InGameAuthHandler(
    private val authHandler: AuthNettyRequestHandler,
    private val authResponseMapper: AuthNettyResponseMapper,
    private val channelRepository: ChannelRepository
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(InGameAuthHandler::class.java)
    }
     fun auth(
        requestData: AuthRequestMessage,
        arkhamusChannel: ArkhamusChannel
    ): AuthGameData? {
        val auth = authHandler.process(requestData, arkhamusChannel)
        val authResponse = authResponseMapper.process(
            auth.userAccount,
            auth.game,
            auth.userOfTheGame
        )
        val responseJson = authResponse.toJson()
        arkhamusChannel.channel.writeAndFlush(responseJson)
        return if (authResponse.message == AuthState.FAIL) {
            logger.error("fake auth request - $requestData")
            channelRepository.closeAndRemove(arkhamusChannel)
            null
        } else {
            auth
        }
    }
}