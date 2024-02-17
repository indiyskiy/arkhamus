package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameDataBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.NettyResponseBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameUserRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRedisRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ArkhamusOneTickLogic(
    private val gameDataBuilder: GameDataBuilder,
    private val nettyResponseBuilder: NettyResponseBuilder,
    private val gameRepository: GameRedisRepository,
    private val gameUserRedisRepository: GameUserRedisRepository,
    private val redisDataAccess: RedisDataAccess
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(ArkhamusOneTickLogic::class.java)
        const val TICK_DELTA = 20 //ms
    }

    fun processCurrentTasks(
        currentTasks: MutableList<NettyTickRequestMessageContainer>,
        tick: Long,
        game: RedisGame
    ): List<NettyResponseMessage> {
        try {
            val globalGameData = redisDataAccess.loadGlobalGameData(game)
            currentTasks.forEach {
                if (isCurrentTick(it, tick)) {
                    process(it, tick, globalGameData)
                }
            }
            val responses = mutableListOf<NettyResponseMessage>()
            val iterator = currentTasks.listIterator()
            while (iterator.hasNext()) {
                val task = iterator.next()
                if (isCurrentTick(task, tick)) {
                    val response = buildResponse(task, globalGameData)
                    responses.add(response)
                    iterator.remove()
                }
            }
            updateNextTick(tick, game)
            return responses
        } catch (e: Exception){
            logger.error("Error processing current tasks: ${e.message}", e)
        }
        return emptyList()
    }

    private fun isCurrentTick(
        it: NettyTickRequestMessageContainer,
        tick: Long
    ) = it.nettyRequestMessage.baseRequestData.tick == tick

    private fun buildResponse(
        request: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
    ): NettyResponseMessage {
        val gameResponse = gameDataBuilder.build(request, globalGameData)
        return nettyResponseBuilder.buildResponse(gameResponse, request, globalGameData)
    }

    private fun process(
        request: NettyTickRequestMessageContainer,
        tick: Long,
        globalGameData: GlobalGameData
    ) {
        val game = globalGameData.game
        val nettyRequestMessage = request.nettyRequestMessage
        logger.info("Process ${nettyRequestMessage.javaClass.simpleName} of game ${game.id} tick $tick")

        val oldGameUser = globalGameData.users[request.userAccount.id]!!

        oldGameUser.x = nettyRequestMessage.baseRequestData.userPosition.x
        oldGameUser.y = nettyRequestMessage.baseRequestData.userPosition.y
        gameUserRedisRepository.save(oldGameUser)
    }

    private fun updateNextTick(tick: Long, game: RedisGame) {
        game.currentTick = tick + 1
        game.globalTimer += TICK_DELTA
        gameRepository.save(game)
    }
}