package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic.Companion.logger
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.loadGlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.*
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyResponse
import org.springframework.stereotype.Component

@Component
class ArkhamusOneTickLogicImpl(
    private val oneTickUserResponses: OneTickUserResponses,
    private val redisDataAccess: RedisDataAccess,
    private val oneTickUserRequests: OneTickUserRequests,
    private val oneTickTick: OneTickTick,
    private val oneTickTimeEvent: OneTickTimeEvent,
    private val onTickAbilityCast: OnTickAbilityCast,
    private val onTickCraftProcess: OnTickCraftProcess,
    private val gameUserRedisRepository: RedisGameUserRepository,
    private val gameRepository: RedisGameRepository,
) : ArkhamusOneTickLogic {

    override fun processCurrentTasks(
        currentTasks: List<NettyTickRequestMessageDataHolder>,
        game: RedisGame
    ): List<NettyResponse> {
        try {
            val globalGameData = redisDataAccess.loadGlobalGameData(game)
            val currentTick = game.currentTick

            oneTickTick.updateNextTick(game)
            val ongoingEvents = oneTickTimeEvent.processTimeEvents(
                globalGameData,
                globalGameData.timeEvents,
                game.globalTimer
            )
            onTickAbilityCast.applyAbilityCasts(
                globalGameData,
                globalGameData.castAbilities,
                game.globalTimer
            )
            onTickCraftProcess.applyCraftProcess(
                globalGameData,
                globalGameData.craftProcess,
                game.globalTimer
            )
            val processedTasks = oneTickUserRequests.processRequests(
                currentTasks,
                currentTick,
                globalGameData,
                ongoingEvents
            )
            val responses =
                oneTickUserResponses.buildResponses(
                    currentTick,
                    globalGameData,
                    processedTasks,
                )
            if(responses.isNotEmpty()){
                game.lastTimeSentResponse = game.lastTimeSentResponse
            }
            saveAllUsers(globalGameData)
            gameRepository.save(game)
            return responses
        } catch (e: Throwable) {
            logger.error("Error processing current tasks: ${e.message}", e)
        }
        return emptyList()
    }

    private fun saveAllUsers(globalGameData: GlobalGameData) {
        globalGameData.users.forEach { gameUser ->
            gameUserRedisRepository.save(gameUser.value)
        }
    }

}