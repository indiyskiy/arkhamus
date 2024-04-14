package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.OpenContainerGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import org.springframework.stereotype.Component

@Component
class OpenContainerNettyResponseMapper : NettyResponseMapper {

    private val itemMap = Item.values().associateBy { it.id }
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == OpenContainerGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): OpenContainerNettyResponse {
        with(requestProcessData as OpenContainerGameData) {
            val mappedItem = this.container.items.map {
                itemMap[it.key]!! to it.value
            }
            val containerCells = mappedItem.map {
                ContainerCell(it.first.id).apply {
                    this.number = it.second
                }
            }
            if(requestProcessData.container.holdingUser == user.id) {
                return myContainer(containerCells, requestProcessData, user, requestProcessData.gameUser!!)
            } else {
                return closedContainer(requestProcessData, user, requestProcessData.gameUser!!)
            }
        }
    }

    private fun myContainer(
        containerCells: List<ContainerCell>,
        gameData: OpenContainerGameData,
        user: UserAccount,
        gameUser: RedisGameUser
    ) = OpenContainerNettyResponse(
        containerCells = containerCells,
        containerState = gameData.container.state,
        holdingUser = gameData.container.holdingUser,
        tick = gameData.tick,
        userId = user.id!!,
        myGameUser = MyGameUserResponseMessage(gameUser),
        otherGameUsers = gameData.otherGameUsers.map {
            NettyGameUserResponseMessage(
                id = it.userId,
                nickName = it.nickName,
                x = it.x,
                y = it.y
            )
        },
        ongoingEvents = gameData.visibleOngoingEvents.map {
            OngoingEventResponse(it)
        }
    )

    private fun closedContainer(
        gameData: OpenContainerGameData,
        user: UserAccount,
        gameUser: RedisGameUser
    ) = OpenContainerNettyResponse(
        containerCells = emptyList(),
        containerState = gameData.container.state,
        holdingUser = null,
        tick = gameData.tick,
        userId = user.id!!,
        myGameUser = MyGameUserResponseMessage(gameUser),
        otherGameUsers = gameData.otherGameUsers.map {
            NettyGameUserResponseMessage(
                id = it.userId,
                nickName = it.nickName,
                x = it.x,
                y = it.y
            )
        },
        ongoingEvents = gameData.visibleOngoingEvents.map {
            OngoingEventResponse(it)
        }
    )

}