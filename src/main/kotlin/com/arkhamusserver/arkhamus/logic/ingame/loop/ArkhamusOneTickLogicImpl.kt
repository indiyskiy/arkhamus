package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic.Companion.logger
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.loadGlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.*
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import org.springframework.stereotype.Component

@Component
class ArkhamusOneTickLogicImpl(
    private val oneTickUserResponses: OneTickUserResponses,
    private val redisDataAccess: RedisDataAccess,
    private val oneTickUserRequests: OneTickUserRequests,
    private val oneTickUser: OneTickUser,
    private val oneTickTick: OneTickTick,
    private val oneTickTimeEvent: OneTickTimeEvent,
    private val oneTickShortTimeEvent: OneTickShortTimeEvent,
    private val onTickAbilityCast: OnTickAbilityCast,
    private val onTickCraftProcess: OnTickCraftProcess,
    private val afterLoopSaving: AfterLoopSavingComponent,
    private val oneTickTryEndGameMaybeHandler: OneTickTryEndGameMaybeHandler,
) : ArkhamusOneTickLogic {

    override fun processCurrentTasks(
        currentTasks: List<NettyTickRequestMessageDataHolder>,
        game: RedisGame
    ): List<NettyResponse> {
        try {
            val globalGameData = redisDataAccess.loadGlobalGameData(game)

            oneTickTick.updateNextTick(game)
            val ongoingEvents = oneTickTimeEvent.processTimeEvents(
                globalGameData,
                globalGameData.timeEvents,
                game.globalTimer
            )
            oneTickShortTimeEvent.processShortTimeEvents(globalGameData.shortTimeEvents)
            onTickAbilityCast.applyAbilityCasts(
                globalGameData,
                globalGameData.castAbilities
            )
            onTickCraftProcess.applyCraftProcess(
                globalGameData,
                globalGameData.craftProcess
            )
            val processedRequests = oneTickUserRequests.processRequests(
                currentTasks,
                globalGameData,
                ongoingEvents,
            )
            oneTickUser.processUsers(globalGameData, ongoingEvents)
            val responses =
                oneTickUserResponses.buildResponses(
                    globalGameData,
                    processedRequests,
                )
            if (responses.isNotEmpty()) {
                game.lastTimeSentResponse = game.globalTimer
            }
            oneTickTryEndGameMaybeHandler.checkIfEnd(game, globalGameData.users.values)
            afterLoopSaving.saveAll(globalGameData, game)

            return responses
        } catch (e: Throwable) {
            logger.error("Error processing current tasks: ${e.message}", e)
        }
        return emptyList()
    }


}