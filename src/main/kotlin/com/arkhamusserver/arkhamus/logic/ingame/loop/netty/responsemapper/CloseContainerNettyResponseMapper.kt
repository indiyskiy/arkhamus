package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.CloseContainerGameData
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
class CloseContainerNettyResponseMapper : NettyResponseMapper {

    private val itemMap = Item.values().associateBy { it.getId() }
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == CloseContainerGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): CloseContainerNettyResponse {
        with(requestProcessData as CloseContainerGameData) {
            return myInventory(
                requestProcessData.sortedInventory ?: emptyList(),
                requestProcessData,
                user,
                requestProcessData.gameUser!!
            )
        }
    }

    private fun myInventory(
        sortedInventory: List<ContainerCell>,
        gameData: CloseContainerGameData,
        user: UserAccount,
        gameUser: RedisGameUser
    ) = CloseContainerNettyResponse(
        userInventory = sortedInventory,
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