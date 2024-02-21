package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.GameNettyLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.exception.entity.ChannelNotFoundException
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.view.dto.netty.request.AuthRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Sharable
class ProcessingHandler(
    private val gameNettyLogic: GameNettyLogic,
    private val inGameAuthHandler: InGameAuthHandler,
    private val inGameStartGameHandler: InGameStartGameHandler,
    private val channelRepository: ChannelRepository
) : SimpleChannelInboundHandler<NettyRequestMessage>() {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ProcessingHandler::class.java)
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        try {
            logger.info("Client joined")
            val arkhamusChannel = ArkhamusChannel(
                channel = ctx.channel(),
                channelId = ctx.channel().id().asLongText()
            )
            channelRepository.put(arkhamusChannel)
        } catch (e: Exception) {
            logger.error("failed to process joined client", e)
        }
    }

    override fun channelRead0(
        context: ChannelHandlerContext,
        requestData: NettyRequestMessage
    ) {
        try {
            val id = context.channel().id().asLongText()
            val arkhamusChannel = channelRepository.get(id) ?: throw ChannelNotFoundException(id)

            if (requestData is AuthRequestMessage) {
                val authData = inGameAuthHandler.auth(requestData, arkhamusChannel)
                authData?.let { inGameStartGameHandler.tryToStartGame(it) }
            }
            val account = arkhamusChannel.userAccount
            if (account == null) {
                logger.error("not authorised")
                channelRepository.closeAndRemove(arkhamusChannel)
            } else {
                process(requestData, arkhamusChannel, account)
            }
        } catch (e: Exception) {
            logger.error("failed to process request", e)
        }
    }

    private fun process(
        requestData: NettyRequestMessage,
        arkhamusChannel: ArkhamusChannel,
        account: UserAccount
    ) {
        if (
            (requestData is NettyBaseRequestMessage) &&
            (arkhamusChannel.gameSession?.state == GameState.IN_PROGRESS)
        ) {
            val nettyTickRequestMessageContainer = NettyTickRequestMessageContainer(
                requestData,
                arkhamusChannel.channelId,
                account,
                arkhamusChannel.gameSession,
                arkhamusChannel.userOfGameSession,
            )
            gameNettyLogic.process(nettyTickRequestMessageContainer)
        }
    }


    /*
	 * In case of exception, close channel. One may choose to custom handle exception
	 * & have alternative logical flows.
	 */
    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("Closing connection for client - $ctx")
        logger.error("Closing connection exception", cause)
        channelRepository.closeAndRemove(ctx.channel().id().asLongText())
    }

}

