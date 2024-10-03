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
    private val actionCacheHandler: ActionCacheHandler
) {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(OneTickUserRequests::class.java)
    }

    fun processRequests(
        currentTasks: List<NettyTickRequestMessageDataHolder>,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>,
    ): List<NettyTickRequestMessageDataHolder> {
        val tasksByUser = currentTasks.groupBy { it.userAccount.id }
        return tasksByUser.map { entry ->
            val taskToProcess = chooseTaskToProcess(entry.value)
            processRequest(taskToProcess, globalGameData, ongoingEvents)
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
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>,
    ) {
        requestContainer.requestProcessData =
            requestProcessDataBuilder.build(requestContainer, globalGameData, ongoingEvents)

        val isAction = actionCacheHandler.isAction(requestContainer)
        val isOldAction = isAction && actionCacheHandler.isOldAction(requestContainer)

        if (!isOldAction) {
            nettyRequestProcessors.filter {
                it.accept(requestContainer)
            }.forEach {
                it.process(requestContainer, globalGameData, ongoingEvents)
            }
            if (isAction) {
                applyNewestAction(requestContainer)
            }
        } else {
            updateCurrentGameDataWithOldAction(requestContainer)
        }
    }

    private fun applyNewestAction(requestContainer: NettyTickRequestMessageDataHolder) {
        actionCacheHandler.applyNewestAction(requestContainer)
    }

    private fun updateCurrentGameDataWithOldAction(requestContainer: NettyTickRequestMessageDataHolder) {
        actionCacheHandler.updateCurrentGameDataWithOldAction(requestContainer)
    }

}