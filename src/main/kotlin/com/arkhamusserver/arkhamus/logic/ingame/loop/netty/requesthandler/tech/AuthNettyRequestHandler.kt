package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.requesthandler.tech

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.tech.AuthRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.DatabaseDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.getOtherGameUsers
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.auth.NettyAuthService
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.GameState.*
import com.arkhamusserver.arkhamus.view.dto.netty.request.tech.AuthRequestMessage
import org.springframework.stereotype.Component

@Component
class AuthNettyRequestHandler(
    private val nettyAuthService: NettyAuthService,
    private val channelRepository: ChannelRepository,
    private val databaseDataAccess: DatabaseDataAccess,
    private val redisDataAccess: RedisDataAccess
) {

    companion object {
        private val allowedStates = setOf(IN_PROGRESS, PENDING, GAME_END_SCREEN)
    }

    fun process(
        nettyRequestMessage: AuthRequestMessage,
        arkhamusChannel: ArkhamusChannel,
    ): AuthRequestProcessData? {
        return nettyAuthService.auth(nettyRequestMessage.token)?.let { account ->
            val userOfTheGame = findUserOfGame(account)
            userOfTheGame?.let {
                val game = databaseDataAccess.findByGameId(it.gameSession.id!!)
                val gameUser = redisDataAccess.getGameUser(
                    it.userAccount.id!!,
                    it.gameSession.id!!
                ) ?: return null
                val otherGameUsers = redisDataAccess.getOtherGameUsers(gameUser.inGameId(), it.gameSession.id!!)
                AuthRequestProcessData(gameUser = gameUser, success = true, otherGameUsers = otherGameUsers).apply {
                    this.userOfTheGame = it
                    this.userAccount = it.userAccount
                    this.game = game
                    this.reason = "Authentication successful"
                }.also { auth ->
                    arkhamusChannel.userAccount = auth.userAccount
                    arkhamusChannel.gameSession = auth.game
                    arkhamusChannel.userOfGameSession = auth.userOfTheGame
                    channelRepository.update(arkhamusChannel)
                }
            } ?: AuthRequestProcessData(
                gameUser = null,
                otherGameUsers = emptyList(),
                success = false,
                reason = "User in game not found"
            )
        } ?: AuthRequestProcessData(
            gameUser = null,
            otherGameUsers = emptyList(),
            success = false,
            reason = "Auth failed"
        )
    }

    private fun findUserOfGame(account: UserAccount): UserOfGameSession? {
        return databaseDataAccess.findByUserAccountIdNotLeft(account.id!!)
            .filter { it.gameSession.state in allowedStates }
            .sortedByDescending { it.gameSession.creationTimestamp }
            .firstOrNull()
    }

}