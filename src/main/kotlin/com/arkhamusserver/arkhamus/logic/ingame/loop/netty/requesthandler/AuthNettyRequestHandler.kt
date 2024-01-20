package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AuthGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameResponseMessage
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.DatabaseDataAccess
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.NettyAuthService
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.view.dto.netty.request.AuthRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyRequestMessage
import org.springframework.stereotype.Component

@Component
class AuthNettyRequestHandler(
    private val nettyAuthService: NettyAuthService,
    private val databaseDataAccess: DatabaseDataAccess
) : NettyRequestHandler {


    override fun acceptClass(nettyRequestMessage: NettyRequestMessage): Boolean =
        nettyRequestMessage::class.java == AuthRequestMessage::class.java

    override fun accept(nettyRequestMessage: NettyRequestMessage): Boolean = true


    override fun process(
        nettyRequestMessage: NettyRequestMessage,
        user: UserAccount?,
        gameSession: GameSession?,
        arkhamusChannel: ArkhamusChannel
    ): GameResponseMessage {
        return with(nettyRequestMessage as AuthRequestMessage) {
            this.token?.let { nettyAuthService.auth(it) }?.let { account ->
                AuthGameResponse().apply {
                    val userOfTheGame = findUserOfGame(account)
                    this.userOfTheGame = userOfTheGame
                    this.userAccount = userOfTheGame?.userAccount
                    this.game = userOfTheGame?.gameSession
                    this.message = "Authentication successful"
                }.also { auth ->
                    arkhamusChannel.userAccount = auth.userAccount
                    arkhamusChannel.gameSession = auth.game
                    arkhamusChannel.userRole = auth.userOfTheGame
                }
            } ?: AuthGameResponse()
        }
    }

    private fun findUserOfGame(account: UserAccount): UserOfGameSession? {
        return account.id?.let {
            databaseDataAccess.findByUserAccountId(it)
                .firstOrNull { game ->
                    game.gameSession.state == GameState.IN_PROGRESS
                }
        }
    }


}