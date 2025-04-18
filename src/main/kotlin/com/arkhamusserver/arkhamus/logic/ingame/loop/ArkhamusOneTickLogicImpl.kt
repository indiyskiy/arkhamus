package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool.Companion.TICK_DELTA
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.InGameDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.loadGlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.*
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.statuses.OneTickUserStatusHandler
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ArkhamusOneTickLogicImpl(
    private val oneTickUserResponses: OneTickUserResponses,
    private val inGameDataAccess: InGameDataAccess,
    private val oneTickUserRequests: OneTickUserRequests,
    private val oneTickUser: OneTickUser,
    private val oneTickTick: OneTickTick,
    private val oneTickLantern: OneTickLantern,
    private val oneTickTimeEvent: OneTickTimeEvent,
    private val oneTickShortTimeEvent: OneTickShortTimeEvent,
    private val onTickAbilityCast: OnTickAbilityCast,
    private val onTickCraftProcess: OnTickCraftProcess,
    private val afterLoopSaving: AfterLoopSavingComponent,
    private val oneTickTryEndGameMaybeHandler: OneTickTryEndGameMaybeHandler,
    private val oneTickUserStatusHandler: OneTickUserStatusHandler,
    private val activityHandler: ActivityHandler
) : ArkhamusOneTickLogic {

    companion object {
//        const val SAVE_ACTIVITY_TICK_DELTA = 60 * 1000 / TICK_DELTA / 8 //8 times per minute aka 20 tps
        const val SAVE_ACTIVITY_TICK_DELTA = 60 * 1000 / TICK_DELTA / 10 //10 times per minute aka 25 tps
        private val logger = LoggerFactory.getLogger(ArkhamusOneTickLogicImpl::class.java)
    }

    override fun processCurrentTasks(
        currentTasks: List<NettyTickRequestMessageDataHolder>,
        game: InRamGame,
    ): List<NettyResponse> {
        try {
            val globalGameData = inGameDataAccess.loadGlobalGameData(game)
            oneTickUserStatusHandler.nullifyOldStatuses(globalGameData)
            val timePassedMillis = oneTickTick.updateNextTick(game)
            val ongoingEvents = oneTickTimeEvent.processTimeEvents(
                globalGameData,
                globalGameData.timeEvents,
                game.globalTimer,
                timePassedMillis
            )
            oneTickShortTimeEvent.processShortTimeEvents(globalGameData.shortTimeEvents, timePassedMillis)
            onTickAbilityCast.applyAbilityCasts(
                globalGameData,
                globalGameData.castAbilities,
                timePassedMillis
            )
            onTickCraftProcess.applyCraftProcess(
                globalGameData,
                globalGameData.craftProcess,
                timePassedMillis
            )
            oneTickLantern.tickLanterns(
                globalGameData,
                timePassedMillis
            )
            val processedRequests = oneTickUserRequests.processRequests(
                currentTasks,
                globalGameData,
                ongoingEvents,
            )
            oneTickUser.processUsers(globalGameData, timePassedMillis)
            if (game.currentTick - game.lastTickSaveHeartbeatActivity > SAVE_ACTIVITY_TICK_DELTA) {
                activityHandler.saveHeartbeatForUsers(globalGameData)
                game.lastTickSaveHeartbeatActivity = game.currentTick
            }
            oneTickUserStatusHandler.updateStatuses(globalGameData)
            val responses = oneTickUserResponses.buildResponses(
                globalGameData,
                processedRequests,
            )
            if (responses.isNotEmpty()) {
                game.lastTimeSentResponse = game.globalTimer
            }
            oneTickTryEndGameMaybeHandler.checkIfEnd(game, globalGameData.users.values, globalGameData.voteSpots)

            game.serverTimeLastTick = game.serverTimeCurrentTick
            afterLoopSaving.saveAll(globalGameData, game)

            return responses
        } catch (e: Throwable) {
            LoggingUtils.error(
                logger,
                LoggingUtils.EVENT_ERROR,
                "Error processing current tasks: ${e.message}",
                e
            )
            LoggingUtils.withContext(
                gameId = game.gameId.toString(),
                eventType = LoggingUtils.EVENT_ERROR
            ) {
                logger.error("Game state at error: tick=${game.currentTick}, gameId=${game.gameId}, state=${game.state}")
            }
        }
        return emptyList()
    }


}
