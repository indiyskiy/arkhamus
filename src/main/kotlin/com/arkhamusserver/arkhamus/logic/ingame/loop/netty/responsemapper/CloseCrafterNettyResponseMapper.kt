package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.CloseCrafterGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import org.springframework.stereotype.Component

@Component
class CloseCrafterNettyResponseMapper : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == CloseCrafterGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): CloseCrafterNettyResponse {
        with(requestProcessData as CloseCrafterGameData) {
            return myInventory(
                sortedInventory = requestProcessData.sortedInventory ?: emptyList(),
                gameData = requestProcessData,
                user = user,
                gameUser = requestProcessData.gameUser!!,
                availableAbilities = requestProcessData.availableAbilities
            )
        }
    }

    private fun myInventory(
        sortedInventory: List<ContainerCell>,
        gameData: CloseCrafterGameData,
        user: UserAccount,
        gameUser: RedisGameUser,
        availableAbilities: List<AbilityOfUserResponse>
    ) = CloseCrafterNettyResponse(
        sortedUserInventory = sortedInventory,
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
        },
        availableAbilities = availableAbilities
    )

}