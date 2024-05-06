package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameDataBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.springframework.stereotype.Component

@Component
class OneTickUserRequests(
    private val nettyRequestProcessors: List<NettyRequestProcessor>,
    private val requestProcessDataBuilder: GameDataBuilder,
) {
    fun processRequests(
        currentTasks: List<NettyTickRequestMessageDataHolder>,
        currentTick: Long,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): List<NettyTickRequestMessageDataHolder> {
        val tasksByUser = currentTasks.groupBy { it.userAccount.id }
        return tasksByUser.map{ entry ->
            val taskToProcess = chooseTaskToProcess(entry.value)
            processRequest(taskToProcess, currentTick, globalGameData, ongoingEvents)
            taskToProcess
        }
    }

    private fun chooseTaskToProcess(userTasks: List<NettyTickRequestMessageDataHolder>): NettyTickRequestMessageDataHolder {
        return userTasks.sortedByDescending { it.nettyRequestMessage.baseRequestData.tick }.first()
    }

    private fun processRequest(
        requestContainer: NettyTickRequestMessageDataHolder,
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