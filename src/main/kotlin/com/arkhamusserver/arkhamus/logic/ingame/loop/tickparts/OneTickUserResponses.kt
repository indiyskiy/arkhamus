package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameDataBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.NettyResponseBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.isCurrentTick
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponseMessage
import org.springframework.stereotype.Component

@Component
class OneTickUserResponses(
    private val gameDataBuilder: GameDataBuilder,
    private val nettyResponseBuilder: NettyResponseBuilder,
) {
    fun buildResponses(
        currentTick: Long,
        globalGameData: GlobalGameData,
        currentTasks: MutableList<NettyTickRequestMessageContainer>,
        ongoingEffects: List<OngoingEvent>
    ): MutableList<NettyResponseMessage> {
        val responses = mutableListOf<NettyResponseMessage>()
        val iterator = currentTasks.listIterator()

        while (iterator.hasNext()) {
            val task = iterator.next()
            if (task.isCurrentTick(currentTick)) {
                val response = buildResponse(task, globalGameData,ongoingEffects)
                responses.add(response)
                iterator.remove()
            }
        }
        return responses
    }

    private fun buildResponse(
        request: NettyTickRequestMessageContainer,
        globalGameData: GlobalGameData,
        ongoingEffects: List<OngoingEvent>
    ): NettyResponseMessage {
        val gameResponse = gameDataBuilder.build(request, globalGameData, ongoingEffects)
        return nettyResponseBuilder.buildResponse(gameResponse, request, globalGameData)
    }
}