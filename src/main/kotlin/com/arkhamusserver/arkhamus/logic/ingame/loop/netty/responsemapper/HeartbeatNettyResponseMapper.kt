package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.HeartbeatGameData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.*
import org.springframework.stereotype.Component

@Component
class HeartbeatNettyResponseMapper : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: GameData): Boolean =
        gameResponseMessage::class.java == HeartbeatGameData::class.java

    override fun accept(gameResponseMessage: GameData): Boolean = true

    override fun process(
        gameData: GameData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): HeartbeatNettyResponse {
        (gameData as HeartbeatGameData).let {
            return HeartbeatNettyResponse(
                tick = it.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponseMessage(
                    id = user.id!!,
                    nickName = user.nickName!!,
                    x = it.gameUser!!.x,
                    y = it.gameUser.y
                ),
                otherGameUsers = it.otherGameUsers.map { gameUser ->
                    NettyGameUserResponseMessage(
                        id = gameUser.userId,
                        nickName = gameUser.nickName,
                        x = gameUser.x,
                        y = gameUser.y
                    )
                },
                ongoingEffects = gameData.visibleOngoingEffects.map {
                    OngoingEventResponse(
                        type = it.event.type
                    )
                }
            )
        }
    }

}