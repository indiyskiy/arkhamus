package com.arkhamusserver.arkhamus.logic.ingame.test

import com.arkhamusserver.arkhamus.logic.CurrentUserService
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
    private val gameRelatedIdSource: GameRelatedIdSource,
    private val currentUserService: CurrentUserService,
) {
    fun getContainerByUserAndId(gameId: Long, containerId: Long): ContainerNettyResponse {
        val user = currentUserService.getCurrentUserAccount()
        return getContainerByUserAndId(gameId, containerId, user.id!!)
    }

    fun getContainerByUserAndId(gameId: Long, containerId: Long, userId: Long): ContainerNettyResponse {
        val game = gameRepository.findById(gameId.toString())
        val container =
            containerRepository.findById(
                gameRelatedIdSource.getId(gameId, containerId)
            ).get()
        return ContainerNettyResponse(
            tick = game.get().currentTick,
            userId = userId
        ).apply {
            containerCells = container.items.map { (itemId, quantity) ->
                NettyContainerCell(itemId.toLong(), quantity)
            }
        }
    }
}