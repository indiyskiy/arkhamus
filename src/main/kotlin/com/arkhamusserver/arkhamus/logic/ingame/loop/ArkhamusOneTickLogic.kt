package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse

interface ArkhamusOneTickLogic {

    fun processCurrentTasks(
        currentTasks: List<NettyTickRequestMessageDataHolder>,
        game: InRamGame,
    ): List<NettyResponse>
}