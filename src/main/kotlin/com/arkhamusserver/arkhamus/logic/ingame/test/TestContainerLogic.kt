package com.arkhamusserver.arkhamus.logic.ingame.test

import com.arkhamusserver.arkhamus.model.dataaccess.redis.ContainerRedisRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.GameRelatedIdSource
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyContainerCell
import org.springframework.stereotype.Component

@Component
class TestContainerLogic(
    private val containerRepository: ContainerRedisRepository,
    private val gameRelatedIdSource: GameRelatedIdSource
) {
    fun getContainerById(gameId: Long, containerId: Long): ContainerNettyResponse {
        val container =
            containerRepository.findById(
                gameRelatedIdSource.getId(gameId,containerId)
            ).get()
        return ContainerNettyResponse().apply {
            containerCells = container.items.map { (itemId, quantity) ->
                NettyContainerCell(itemId.toLong(), quantity)
            }
        }
    }
}