package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AuthGameResponse
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.DatabaseDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.NettyAuthService
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.view.dto.netty.request.AuthRequestMessage
import org.springframework.stereotype.Component

@Component
class AuthNettyRequestHandler(
    private val nettyAuthService: NettyAuthService,
    private val channelRepository: ChannelRepository,
    private val databaseDataAccess: DatabaseDataAccess,
    private val redisDataAccess: RedisDataAccess
) {

    fun process(
        nettyRequestMessage: AuthRequestMessage,
        arkhamusChannel: ArkhamusChannel,
    ): AuthGameResponse {
        return nettyAuthService.auth(nettyRequestMessage.token)?.let { account ->
            val userOfTheGame = findUserOfGame(account)
            val game = databaseDataAccess.findByGameId(userOfTheGame.gameSession.id!!)
            val gameUser = redisDataAccess.getGameUser(userOfTheGame.userAccount.id!!, userOfTheGame.gameSession.id!!)
            val otherGameUsers = redisDataAccess.getOtherGameUsers(gameUser.id, userOfTheGame.gameSession.id!!)
            AuthGameResponse(gameUser = gameUser, otherGameUsers = otherGameUsers).apply {
                this.userOfTheGame = userOfTheGame
                this.userAccount = userOfTheGame.userAccount
                this.game = game
                this.message = "Authentication successful"
            }.also { auth ->
                arkhamusChannel.userAccount = auth.userAccount
                arkhamusChannel.gameSession = auth.game
                arkhamusChannel.userOfGameSession = auth.userOfTheGame
                channelRepository.update(arkhamusChannel)
            }
        } ?: AuthGameResponse(gameUser = null, otherGameUsers = emptyList())
    }

    private fun findUserOfGame(account: UserAccount): UserOfGameSession {
        return account.id!!.let {
            databaseDataAccess.findByUserAccountId(it)
                .first { game ->
                    game.gameSession.state == GameState.IN_PROGRESS
                }
        }
    }


}