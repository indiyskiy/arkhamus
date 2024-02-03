package com.arkhamusserver.arkhamus.logic.ingame.test

import com.arkhamusserver.arkhamus.model.dataaccess.redis.ContainerRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyContainerCell
import org.springframework.stereotype.Component

@Component
class TestContainerLogic(
    private val containerRepository: ContainerRedisRepository,
    private val gameRepository: RedisGameRepository,
    private val gameRelatedIdSource: GameRelatedIdSource
) {
    fun getContainerById(gameId: Long, containerId: Long): ContainerNettyResponse {
        val game = gameRepository.findById(gameId.toString())
        val container =
            containerRepository.findById(
                gameRelatedIdSource.getId(gameId, containerId)
            ).get()
        return ContainerNettyResponse(
            tick = game.get().currentTick
        ).apply {
            containerCells = container.items.map { (itemId, quantity) ->
                NettyContainerCell(itemId.toLong(), quantity)
            }
        }
    }
}