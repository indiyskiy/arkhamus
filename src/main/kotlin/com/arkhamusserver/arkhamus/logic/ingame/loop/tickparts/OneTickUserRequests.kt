package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.isCurrentTick
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageContainer
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import org.springframework.stereotype.Component

@Component
class OneTickUserRequests(
    private val gameUserRedisRepository: RedisGameUserRepository,
) {
    fun processRequests(
        currentTasks: MutableList<NettyTickRequestMessageContainer>,
        currentTick: Long,
        globalGameData: GlobalGameData,
        ongoingEffects: List<OngoingEvent>
    ) {
        currentTasks.forEach {
            if (it.isCurrentTick(currentTick)) {
                processRequest(it, currentTick, globalGameData, ongoingEffects)
            }
        }
    }

    private fun processRequest(
        request: NettyTickRequestMessageContainer,
        tick: Long,
        globalGameData: GlobalGameData,
        ongoingEvents: List<OngoingEvent>
    ) {
        val game = globalGameData.game
        val nettyRequestMessage = request.nettyRequestMessage
        ArkhamusOneTickLogic.logger.info("Process ${nettyRequestMessage.javaClass.simpleName} of game ${game.id} tick $tick")

        val oldGameUser = globalGameData.users[request.userAccount.id]!!
        oldGameUser.x = nettyRequestMessage.baseRequestData.userPosition.x
        oldGameUser.y = nettyRequestMessage.baseRequestData.userPosition.y
        gameUserRedisRepository.save(oldGameUser)
    }

}