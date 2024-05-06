package com.arkhamusserver.arkhamus.logic.ingame.test

import com.arkhamusserver.arkhamus.logic.CurrentUserService
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.RedisDataAccess
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.netcode.getOtherGameUsers
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.MyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.OpenContainerNettyResponse
import org.springframework.stereotype.Component

@Component
class TestContainerLogic(
    private val currentUserService: CurrentUserService,
    private val redisDataAccess: RedisDataAccess
) {
    fun getContainerByUserAndId(gameId: Long, containerId: Long): OpenContainerNettyResponse? {
        val user = currentUserService.getCurrentUserAccount()
        return getContainerByUserAndId(gameId, containerId, user.id!!)
    }

    fun getContainerByUserAndId(gameId: Long, containerId: Long, userId: Long): OpenContainerNettyResponse? {
        val game = redisDataAccess.getGame(gameId) ?: return null
        val container = redisDataAccess.getContainer(
            containerId, gameId
        ) ?: return null
        val user = redisDataAccess.getGameUser(
            userId,
            gameId
        ) ?: return null
        val otherUsers = redisDataAccess.getOtherGameUsers(userId, gameId)

        return OpenContainerNettyResponse(
            tick = game.currentTick,
            userId = userId,
            myGameUser = MyGameUserResponseMessage(user),
            otherGameUsers = otherUsers.map {
                NettyGameUserResponseMessage(
                    id = it.userId,
                    nickName = it.nickName,
                    x = it.x,
                    y = it.y
                )
            },
            ongoingEvents = emptyList(),
            containerState = container.state,
            containerCells = container.items.map { (itemId, quantity) ->
                ContainerCell(itemId, quantity)
            },
            holdingUser = container.holdingUser,
            availableAbilities = emptyList(),
            userInventory = emptyList()
        )
    }
}