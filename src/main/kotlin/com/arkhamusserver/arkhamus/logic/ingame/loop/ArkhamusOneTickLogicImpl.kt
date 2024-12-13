package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool.Companion.TICK_DELTA
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.loadGlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.*
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ArkhamusOneTickLogicImpl(
    private val oneTickUserResponses: OneTickUserResponses,
    private val redisDataAccess: RedisDataAccess,
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
    private val activityHandler: ActivityHandler
) : ArkhamusOneTickLogic {

    companion object {
        const val SAVE_ACTIVITY_TICK_DELTA = 60 * 1000 / TICK_DELTA / 8 //8 times per minute
        private var logger = LoggerFactory.getLogger(ArkhamusOneTickLogicImpl::class.java)
    }

    override fun processCurrentTasks(
        currentTasks: List<NettyTickRequestMessageDataHolder>,
        game: RedisGame,
    ): List<NettyResponse> {
        try {
//            logger.info("loadGlobalGameData")
            val globalGameData = redisDataAccess.loadGlobalGameData(game)
//            logger.info("updateNextTick")
            val timePassedMillis = oneTickTick.updateNextTick(game)
//            logger.info("processTimeEvents")
            val ongoingEvents = oneTickTimeEvent.processTimeEvents(
                globalGameData,
                globalGameData.timeEvents,
                game.globalTimer,
                timePassedMillis
            )
//            logger.info("processShortTimeEvents")
            oneTickShortTimeEvent.processShortTimeEvents(globalGameData.shortTimeEvents, timePassedMillis)
//            logger.info("applyAbilityCasts")
            onTickAbilityCast.applyAbilityCasts(
                globalGameData,
                globalGameData.castAbilities,
                timePassedMillis
            )
//            logger.info("applyCraftProcess")
            onTickCraftProcess.applyCraftProcess(
                globalGameData,
                globalGameData.craftProcess,
                timePassedMillis
            )
//            logger.info("tickLanterns")
            oneTickLantern.tickLanterns(
                globalGameData,
                timePassedMillis
            )
//            logger.info("processRequests")
            val processedRequests = oneTickUserRequests.processRequests(
                currentTasks,
                globalGameData,
                ongoingEvents,
            )
//            logger.info("processUsers")
            oneTickUser.processUsers(globalGameData, timePassedMillis)
            if (game.currentTick - game.lastTickSaveHeartbeatActivity > SAVE_ACTIVITY_TICK_DELTA) {
//                logger.info("saveHeartbeatForUsers")
                activityHandler.saveHeartbeatForUsers(globalGameData)
                game.lastTickSaveHeartbeatActivity = game.currentTick
            }
//            logger.info("buildResponses")
            val responses =
                oneTickUserResponses.buildResponses(
                    globalGameData,
                    processedRequests,
                )
            if (responses.isNotEmpty()) {
                game.lastTimeSentResponse = game.globalTimer
            }
//            logger.info("checkIfEnd")
            oneTickTryEndGameMaybeHandler.checkIfEnd(game, globalGameData.users.values, globalGameData.voteSpots)
            game.serverTimeLastTick = game.serverTimeCurrentTick
            afterLoopSaving.saveAll(globalGameData, game)

            return responses
        } catch (e: Throwable) {
            logger.error("Error processing current tasks: ${e.message}", e)
        }
        return emptyList()
    }


}