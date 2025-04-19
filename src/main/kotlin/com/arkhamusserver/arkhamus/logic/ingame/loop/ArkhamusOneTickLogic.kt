package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse

interface ArkhamusOneTickLogic {

    companion object {
        private val logger = LoggingUtils.getLogger<ArkhamusOneTickLogic>()
    }

    fun processCurrentTasks(
        currentTasks: List<NettyTickRequestMessageDataHolder>,
        game: InRamGame,
    ): List<NettyResponse>
}