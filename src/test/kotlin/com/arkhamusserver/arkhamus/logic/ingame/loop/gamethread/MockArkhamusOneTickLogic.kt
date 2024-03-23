package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.loadGlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.OneTickUserResponses
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
@Primary
class MockArkhamusOneTickLogic(
    private val oneTickUserResponses: OneTickUserResponses,
    private val redisDataAccess: MockRedisDataAccess
): ArkhamusOneTickLogic {

    // ms
    private var processingDelay: Long = 0
    private var processingId = AtomicInteger(0)

    override fun processCurrentTasks(
        currentTasks: MutableList<NettyTickRequestMessageContainer>,
        game: RedisGame
    ): List<NettyResponseMessage> {
        val currentId = processingId.getAndIncrement()
        println("Processing $currentId: received tasks $currentTasks for processing")
        if (processingDelay > 0) {
            Thread.sleep(processingDelay);
        }

        val currentTick = game.currentTick
        val globalGameData = redisDataAccess.loadGlobalGameData(game)
        game.currentTick++
        val responses = oneTickUserResponses.buildResponses(
            currentTick,
            globalGameData,
            currentTasks
        )
        println("Processing $currentId finished: $responses")
        return responses
    }

    fun reset() {
        processingDelay = 0
    }

    fun setDelay(processingDelay: Long) {
        this.processingDelay = processingDelay
    }
}