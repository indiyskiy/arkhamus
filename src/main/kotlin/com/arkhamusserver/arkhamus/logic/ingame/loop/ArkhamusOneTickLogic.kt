package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameResponseBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.NettyResponseBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameUserRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ArkhamusOneTickLogic(
    private val gameResponseBuilder: GameResponseBuilder,
    private val nettyResponseBuilder: NettyResponseBuilder,
    private val gameRepository: RedisGameRepository,
    private val gameUserRedisRepository: GameUserRedisRepository,
    private val gameRelatedIdSource: GameRelatedIdSource
) {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ArkhamusOneTickLogic::class.java)
    }

    fun processCurrentTasks(
        currentTasks: MutableList<NettyTickRequestMessageContainer>,
        tick: Long,
        game: RedisGame
    ): List<NettyResponseMessage> {
        currentTasks.forEach {
            if (isCurrentTick(it, tick)) {
                process(it, tick, game)
            }
        }
        val responses = mutableListOf<NettyResponseMessage>()
        val iterator = currentTasks.listIterator()
        var newTime = 0L
        while (iterator.hasNext()) {
            val task = iterator.next()
            if (isCurrentTick(task, tick)) {
                val response = buildResponse(task, tick, game)
                responses.add(response)
                if (newTime < task.registrationTime) {
                    newTime = task.registrationTime
                }
                iterator.remove()
            }
        }
        updateNextTick(tick, newTime, game)
        return responses
    }

    private fun isCurrentTick(
        it: NettyTickRequestMessageContainer,
        tick: Long
    ) = it.nettyRequestMessage.baseRequestData().tick == tick

    private fun buildResponse(
        request: NettyTickRequestMessageContainer,
        tick: Long,
        game: RedisGame,
    ): NettyResponseMessage {
        val gameResponse = gameResponseBuilder.buildResponse(request, tick, game)
        return nettyResponseBuilder.buildResponse(gameResponse, request, tick, game)
    }

    private fun process(
        request: NettyTickRequestMessageContainer,
        tick: Long,
        game: RedisGame
    ) {
        val nettyRequestMessage = request.nettyRequestMessage
        logger.info("Process ${nettyRequestMessage.javaClass.simpleName} of game ${game.id} tick $tick")
         val oldGameUser = gameUserRedisRepository.findById(
            gameRelatedIdSource.getId(
                game.id,
                request.userAccount.id!!
            )
        ).get()
        oldGameUser.x = nettyRequestMessage.baseRequestData().userPosition.x
        oldGameUser.y = nettyRequestMessage.baseRequestData().userPosition.y
        gameUserRedisRepository.save(oldGameUser)
    }

    private fun updateNextTick(tick: Long, newTime: Long, game: RedisGame) {
        game.currentTick = tick + 1
        game.globalTimer = newTime
        gameRepository.save(game)
    }
}