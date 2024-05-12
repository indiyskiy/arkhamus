package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameDataBuilder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.NettyRequestProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OneTickUserRequests(
    private val nettyRequestProcessors: List<NettyRequestProcessor>,
    private val requestProcessDataBuilder: GameDataBuilder,
) {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(OneTickUserRequests::class.java)
    }

    fun processRequests(
        currentTasks: List<NettyTickRequestMessageDataHolder>,
        currentTick: Long,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ): List<NettyTickRequestMessageDataHolder> {
        val tasksByUser = currentTasks.groupBy { it.userAccount.id }
        logger.info("Process tasks of game ${globalGameData.game.id} tick $currentTick")
        return tasksByUser.map { entry ->
            val taskToProcess = chooseTaskToProcess(entry.value)
            processRequest(taskToProcess, currentTick, globalGameData, ongoingEvents)
            taskToProcess
        }
    }

    private fun chooseTaskToProcess(
        userTasks: List<NettyTickRequestMessageDataHolder>
    ): NettyTickRequestMessageDataHolder {
        return userTasks.maxByOrNull { it.nettyRequestMessage.baseRequestData.tick }!!
    }

    private fun processRequest(
        requestContainer: NettyTickRequestMessageDataHolder,
        tick: Long,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val game = globalGameData.game
        val nettyRequestMessage = requestContainer.nettyRequestMessage
        logger.info("Process ${nettyRequestMessage.type} of game ${game.id} tick $tick")
        requestContainer.requestProcessData =
            requestProcessDataBuilder.build(requestContainer, globalGameData, ongoingEvents)
        nettyRequestProcessors.filter {
            it.accept(requestContainer)
        }.forEach {
            it.process(requestContainer, globalGameData, ongoingEvents)
        }
    }

}