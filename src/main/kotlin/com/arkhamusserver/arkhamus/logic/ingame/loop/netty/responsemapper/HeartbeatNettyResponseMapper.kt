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
        with(gameData as HeartbeatGameData) {
            return HeartbeatNettyResponse(
                tick = nettyRequestMessage.baseRequestData.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponseMessage(
                    id = user.id!!,
                    nickName = user.nickName!!,
                    x = gameData.gameUser!!.x,
                    y = gameData.gameUser.y
                ),
                otherGameUsers = gameData.otherGameUsers.map {
                    NettyGameUserResponseMessage(
                        id = it.userId,
                        nickName = it.nickName,
                        x = it.x,
                        y = it.y
                    )
                }
            )
        }
    }

}