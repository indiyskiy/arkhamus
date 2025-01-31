package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool.Companion.logger
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.InGameDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.ResponseSendingLoopManager
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameTasksProcessor(
    private val inGameDataAccess: InGameDataAccess,
    private val responseSendingLoopManager: ResponseSendingLoopManager,
    private val tickLogic: ArkhamusOneTickLogic,
) {

    @Transactional
    fun processGameTasks(gameId: Long, taskCollection: TaskCollection?) {
        taskCollection?.let { taskCollectionNotNull ->
            try {
                val taskList = synchronized(taskCollectionNotNull) {
                    taskCollectionNotNull.getList().also { taskCollectionNotNull.resetList() }
                }
                val inRamGame = inGameDataAccess.getGame(gameId)
                // TODO better state handling - e.g. we might want to support pause somehow and other stuff later
                if (inRamGame != null && inRamGame.state in GameState.gameInProgressStateStrings) {
                    processGameTick(
                        tasks = taskList,
                        gameId = gameId,
                        ongoingGame = inRamGame,
                    )
                }
            } catch (e: Throwable) {
                logger.error("error on processing game tasks for game $gameId", e)
//            throw e
            }
        }
    }

    private fun processGameTick(
        tasks: List<NettyTickRequestMessageDataHolder>,
        gameId: Long,
        ongoingGame: InRamGame,
    ) {
        try {
            val responses = tickLogic.processCurrentTasks(
                tasks,
                ongoingGame,
            )
            responseSendingLoopManager.addResponses(responses, gameId)
        } catch (e: Throwable) {
            logger.error("error on processing game tick for game $gameId", e)
            throw e
        }
    }
}