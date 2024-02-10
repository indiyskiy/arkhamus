package com.arkhamusserver.arkhamus.logic.ingame.loop.netty

import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GameNettyLogic(
    val gameThreadPool: GameThreadPool
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameNettyLogic::class.java)
    }

    fun process(
        nettyTickRequestMessageContainer: NettyTickRequestMessageContainer
    ) {
        try {
            gameThreadPool.addTask(nettyTickRequestMessageContainer)
        } catch (exception: Exception) {
            logger.error("error on processing request", exception)
        }
    }
}