package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameDataBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component

@Component
class OneTickUserRequests(
    private val nettyRequestProcessors: List<NettyRequestProcessor>,
    private val requestProcessDataBuilder: GameDataBuilder,
) {
    fun processRequests(
        currentTasks: List<NettyTickRequestMessageContainer>,
        currentTick: Long,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        currentTasks.forEach {
            //TODO conflict resolution
            processRequest(it, currentTick, globalGameData, ongoingEvents)
        }
    }

    private fun processRequest(
        requestContainer: NettyTickRequestMessageContainer,
        tick: Long,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val game = globalGameData.game
        val nettyRequestMessage = requestContainer.nettyRequestMessage
        ArkhamusOneTickLogic.logger.info("Process ${nettyRequestMessage.javaClass.simpleName} of game ${game.id} tick $tick")
        requestContainer.requestProcessData =  requestProcessDataBuilder.build(requestContainer, globalGameData, ongoingEvents)
        nettyRequestProcessors.filter {
            it.accept(requestContainer)
        }.forEach {
            it.process(requestContainer, globalGameData, ongoingEvents)
        }
    }

}