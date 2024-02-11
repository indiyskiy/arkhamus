package com.arkhamusserver.arkhamus.logic.ingame.test

import com.arkhamusserver.arkhamus.logic.CurrentUserService
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.MyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyContainerCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameUserResponseMessage
import org.springframework.stereotype.Component

@Component
class TestContainerLogic(
    private val currentUserService: CurrentUserService,
    private val redisDataAccess: RedisDataAccess
) {
    fun getContainerByUserAndId(gameId: Long, containerId: Long): ContainerNettyResponse {
        val user = currentUserService.getCurrentUserAccount()
        return getContainerByUserAndId(gameId, containerId, user.id!!)
    }

    fun getContainerByUserAndId(gameId: Long, containerId: Long, userId: Long): ContainerNettyResponse {
        val game = redisDataAccess.getGame(gameId)
        val container = redisDataAccess.getContainer(
            containerId, gameId
        )
        val user = redisDataAccess.getGameUser(
            userId,
            gameId
        )
        val otherUsers = redisDataAccess.getOtherGameUsers(userId, gameId)

        return ContainerNettyResponse(
            tick = game.currentTick,
            userId = userId,
            myGameUser = MyGameUserResponseMessage(
                userId,
                user.nickName!!,
                user.x!!,
                user.y!!
            ),
            otherGameUsers = otherUsers.map {
                NettyGameUserResponseMessage(
                    id = it.userId!!,
                    nickName = it.nickName!!,
                    x = it.x!!,
                    y = it.y!!
                )
            }
        ).apply {
            containerCells = container.items.map { (itemId, quantity) ->
                NettyContainerCell(itemId.toLong(), quantity)
            }
        }
    }
}