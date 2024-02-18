package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.GameNettyLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AuthGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.exception.entity.ChannelNotFoundException
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.AuthNettyRequestHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.AuthNettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.AuthState
import com.arkhamusserver.arkhamus.view.dto.netty.request.AuthRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.MyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameStartedResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameUserResponseMessage
import com.google.gson.Gson
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Sharable
class ProcessingHandler(
    private val gameNettyLogic: GameNettyLogic,
    private val authHandler: AuthNettyRequestHandler,
    private val authResponseMapper: AuthNettyResponseMapper,
) : SimpleChannelInboundHandler<NettyRequestMessage>() {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ProcessingHandler::class.java)
    }

    @Autowired
    private lateinit var channelRepository: ChannelRepository

    val gson = Gson()

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
                val authData = auth(requestData, arkhamusChannel)
                tryToStartGame(authData)
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

    private fun tryToStartGame(
        authData: AuthGameData?
    ) {
        logger.info("try to start the gamer after auth {}", authData)
        authData?.game?.id?.let { gameId ->
            val channels = channelRepository.getByGameId(gameId)
            if (allUsersAuthorised(channels, authData)) {
                logger.info("all users authorised")
                val user = authData.gameUser
                val users = authData.otherGameUsers

                channels.map {
                    it.channel to NettyGameStartedResponse(
                        userId = it.userAccount!!.id!!,
                        myGameUser = MyGameUserResponseMessage(user!!),
                        allGameUsers = users.map {
                            NettyGameUserResponseMessage(it)
                        }
                    ).toJson()
                }.forEach {
                    it.first.writeAndFlush(it.second)
                }
            } else {
                logger.info("not all users authorised, still waiting")
            }
        }
    }

    private fun allUsersAuthorised(
        channels: List<ArkhamusChannel>,
        authData: AuthGameData
    ) = (channels.mapNotNull { it.userAccount?.id }
        .toSet() == authData.game?.usersOfGameSession?.mapNotNull { it.userAccount.id }?.toSet())

    private fun process(
        requestData: NettyRequestMessage,
        arkhamusChannel: ArkhamusChannel,
        account: UserAccount
    ) {
        if (requestData is NettyBaseRequestMessage) {
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

    private fun auth(
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

    private fun Any.toJson(): String =
        gson.toJson(this) + "\r\n"

}

