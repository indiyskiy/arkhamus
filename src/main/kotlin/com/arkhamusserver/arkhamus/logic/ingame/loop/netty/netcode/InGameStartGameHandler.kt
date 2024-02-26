package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.globalutils.toJson
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AuthGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.MyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameStartedResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameUserResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InGameStartGameHandler(
    private val channelRepository: ChannelRepository,
    private val redisGameRedisRepository: RedisGameRepository,
    private val gameSessionRepository: GameSessionRepository
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(InGameStartGameHandler::class.java)
    }

    fun tryToStartGame(
        authData: AuthGameData
    ) {
        ProcessingHandler.logger.info("try to start the gamer after auth {}", authData)
        authData.game?.id?.let { gameId ->
            val channels = channelRepository.getByGameId(gameId)
            if (allUsersAuthorised(channels, authData)) {
                startTheGame(authData, channels)
            } else {
                logger.info("not all users authorised, still waiting")
            }
        }
    }

    private fun startTheGame(
        authData: AuthGameData,
        channels: List<ArkhamusChannel>
    ) {
        logger.info("all users authorised")
        val user = authData.gameUser
        val users = authData.otherGameUsers

        updateGameSession(authData)
        updateGame(authData)
        notifyUsers(channels, user, users)
    }

    private fun updateGame(authData: AuthGameData) {
        val game = redisGameRedisRepository.findById(authData.game!!.id.toString()).get()
        game.currentTick = 0
        game.state = GameState.IN_PROGRESS.name
        redisGameRedisRepository.save(game)
    }

    private fun updateGameSession(authData: AuthGameData) {
        val gameSession = gameSessionRepository.findById(authData.game!!.id!!).get()
        gameSession.state = GameState.IN_PROGRESS
        gameSessionRepository.save(gameSession)
        channelRepository.updateGame(gameSession)
    }

    private fun notifyUsers(
        channels: List<ArkhamusChannel>,
        user: RedisGameUser?,
        users: List<RedisGameUser>
    ) {
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
    }

    private fun allUsersAuthorised(
        channels: List<ArkhamusChannel>,
        authData: AuthGameData
    ) = (channels.mapNotNull { it.userAccount?.id }
        .toSet() == authData.game?.usersOfGameSession?.mapNotNull { it.userAccount.id }?.toSet())

}