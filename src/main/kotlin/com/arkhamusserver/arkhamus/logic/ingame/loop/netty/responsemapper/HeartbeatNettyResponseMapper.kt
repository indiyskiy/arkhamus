package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.HeartbeatGameData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import org.springframework.stereotype.Component

@Component
class HeartbeatNettyResponseMapper : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == HeartbeatGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): HeartbeatNettyResponse {
        (requestProcessData as HeartbeatGameData).let {
            return HeartbeatNettyResponse(
                tick = it.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponseMessage(it.gameUser!!),
                otherGameUsers = it.otherGameUsers.map { gameUser ->
                    NettyGameUserResponseMessage(
                        id = gameUser.userId,
                        nickName = gameUser.nickName,
                        x = gameUser.x,
                        y = gameUser.y
                    )
                },
                ongoingEvents = requestProcessData.visibleOngoingEvents.map {
                    OngoingEventResponse(it)
                },
                availableAbilities = requestProcessData.availableAbilities
            )
        }
    }

}