package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.GameNettyLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.exception.entity.ChannelNotFoundException
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.GameState.PENDING
import com.arkhamusserver.arkhamus.view.dto.netty.request.tech.AuthRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.ReadTimeoutException
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
                channelId = ctx.channelId()
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
            val id = context.channelId()
            val arkhamusChannel = channelRepository.get(id) ?: throw ChannelNotFoundException(id)

            if (requestData is AuthRequestMessage) {
                auth(requestData, arkhamusChannel)
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

    private fun auth(
        requestData: AuthRequestMessage,
        arkhamusChannel: ArkhamusChannel
    ) {
        val authData = inGameAuthHandler.auth(requestData, arkhamusChannel)
        authData?.let {
            if (it.game?.state == PENDING) {
                inGameStartGameHandler.tryToStartGame(it)
            }
            // TODO maybe else branch to avoid redundancy?
            it.userOfTheGame?.let { user -> gameNettyLogic.markPlayerConnected(user) }
        }
    }

    private fun process(
        requestData: NettyRequestMessage,
        arkhamusChannel: ArkhamusChannel,
        account: UserAccount,
    ) {
        if (
            (requestData is NettyBaseRequestMessage) &&
            (arkhamusChannel.gameSession?.state in GameState.gameInProgressStates)
        ) {
            val nettyTickRequestMessageDataHolder = NettyTickRequestMessageDataHolder(
                nettyRequestMessage = requestData,
                channelId = arkhamusChannel.channelId,
                userAccount = account,
                lastExecutedAction = arkhamusChannel.lastExecutedAction,
                gameSession = arkhamusChannel.gameSession,
                userRole = arkhamusChannel.userOfGameSession,
            )
            gameNettyLogic.process(nettyTickRequestMessageDataHolder)
        } else {
            logger.warn("game not started/already ended or ${account.id} sent shit")
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
        // we may have already disabled the channel in channelInactive
        channelRepository.get(ctx.channelId())?.let { markChannelInactive(it) }
        if (cause is ReadTimeoutException) {
            //TODO handle read timeout exception differently maybe?
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        super.channelInactive(ctx)
        // we may have already disabled the channel in exceptionCaught
        channelRepository.get(ctx.channelId())?.let { markChannelInactive(it) }
    }

    private fun markChannelInactive(arkhamusChannel: ArkhamusChannel) {
        channelRepository.closeAndRemove(arkhamusChannel.channelId)
        val user = arkhamusChannel.userOfGameSession
        if (user == null) {
            logger.warn("channel.userOfGameSession is null, channel = $arkhamusChannel")
        } else {
            gameNettyLogic.markPlayerDisconnected(user)
        }
    }

}

fun ChannelHandlerContext.channelId(): String = channel().id().asLongText()
