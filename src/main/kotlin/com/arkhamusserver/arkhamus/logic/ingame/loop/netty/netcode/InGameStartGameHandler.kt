package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode

import com.arkhamusserver.arkhamus.config.netty.ChannelRepository
import com.arkhamusserver.arkhamus.globalutils.toJson
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.ArkhamusChannel
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.tech.AuthRequestProcessData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InRamGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameStartedResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.GameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class InGameStartGameHandler(
    private val channelRepository: ChannelRepository,
    private val inRamGameRepository: InRamGameRepository,
    private val gameSessionRepository: GameSessionRepository
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(InGameStartGameHandler::class.java)
    }

    @Transactional
    fun tryToStartGame(
        authData: AuthRequestProcessData
    ) {
        ProcessingHandler.logger.info("try to start the gamer after auth {}", authData.userAccount?.id)
        authData.game?.id?.let { gameId ->
            val channels = channelRepository.getByGameId(gameId)
            if (allUsersAuthorised(channels, authData)) {
                if (gamePending(authData)) {
                    startTheGame(authData, channels)
                } else {
                    logger.info("Non-pending game state ${authData.game?.state} when user account ${authData.userAccount?.id} connected")
                }
            } else {
                logger.info("not all users authorised, still waiting")
            }
        }
    }

    private fun startTheGame(
        authData: AuthRequestProcessData,
        channels: List<ArkhamusChannel>
    ) {
        logger.info("all users authorised")
        val user = authData.gameUser
        val users = authData.otherGameUsers

        updateGameSession(authData)
        updateGameOnStart(authData)
        notifyUsers(channels, user, users)
    }

    private fun updateGameOnStart(authData: AuthRequestProcessData) {
        val game = inRamGameRepository.findById(authData.game!!.id.toString()).get()
        game.currentTick = 0
        game.serverTimeLastTick = System.currentTimeMillis()
        game.serverTimeCurrentTick = game.serverTimeLastTick
        game.state = GameState.IN_PROGRESS.name
        inRamGameRepository.save(game)
    }

    private fun updateGameSession(authData: AuthRequestProcessData) {
        val gameSession = gameSessionRepository.findById(authData.game!!.id!!).get()
        gameSession.state = GameState.IN_PROGRESS
        gameSessionRepository.save(gameSession)
        channelRepository.updateGame(gameSession)
    }

    private fun notifyUsers(
        channels: List<ArkhamusChannel>,
        user: InGameUser?,
        users: List<InGameUser>,
    ) {
        channels.map {
            it.channel to NettyGameStartedResponse(
                userId = it.userAccount!!.id!!,
                myGameUser = MyGameUserResponse(user!!, emptyList()),
                allGameUsers = users.map { gameUser ->
                    GameUserResponse(gameUser)
                }
            ).toJson()
        }.forEach {
            it.first.writeAndFlush(it.second)
        }
    }

    private fun allUsersAuthorised(
        channels: List<ArkhamusChannel>,
        authData: AuthRequestProcessData
    ) = (channels.mapNotNull { it.userAccount?.id }.toSet() ==
            authData.game?.usersOfGameSession
                ?.filter { !it.leftTheLobby }
                ?.mapNotNull { it.userAccount.id }
                ?.toSet())

    private fun gamePending(authData: AuthRequestProcessData) = authData.game?.state == GameState.PENDING
}