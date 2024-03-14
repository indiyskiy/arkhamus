package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic.Companion.logger
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.loadGlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.OneTickTick
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.OneTickTimeEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.OneTickUserRequests
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.OneTickUserResponses
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.springframework.stereotype.Component

@Component
class ArkhamusOneTickLogicImpl(
    private val oneTickUserResponses: OneTickUserResponses,
    private val redisDataAccess: RedisDataAccess,
    private val oneTickUserRequests: OneTickUserRequests,
    private val oneTickTick: OneTickTick,
    private val oneTickTimeEvent: OneTickTimeEvent
): ArkhamusOneTickLogic {

    override fun processCurrentTasks(
        currentTasks: MutableList<NettyTickRequestMessageContainer>,
        game: RedisGame
    ): List<NettyResponseMessage> {
        try {
            val globalGameData = redisDataAccess.loadGlobalGameData(game)
            val currentTick = game.currentTick

            oneTickTick.updateNextTick(game)
            val ongoingEffect = oneTickTimeEvent.processTimeEvents(game, globalGameData.timeEvents, game.globalTimer)
            oneTickUserRequests.processRequests(currentTasks, currentTick, globalGameData, ongoingEffect)

            val responses =
                oneTickUserResponses.buildResponses(currentTick, globalGameData, currentTasks, ongoingEffect)
            return responses
        } catch (e: Exception) {
            logger.error("Error processing current tasks: ${e.message}", e)
        }
        return emptyList()
    }
}

fun NettyTickRequestMessageContainer.isCurrentTick(
    tick: Long
) = nettyRequestMessage.baseRequestData.tick == tick