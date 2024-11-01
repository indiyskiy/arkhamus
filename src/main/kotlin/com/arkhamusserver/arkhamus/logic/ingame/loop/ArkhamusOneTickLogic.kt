package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface ArkhamusOneTickLogic {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(ArkhamusOneTickLogic::class.java)
    }

    fun processCurrentTasks(
        currentTasks: List<NettyTickRequestMessageDataHolder>,
        game: RedisGame,
    ): List<NettyResponse>
}