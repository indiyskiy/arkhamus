package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.AbilityGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.ContainerGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.GameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse.HeartbeatGameData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.AbilityNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.MyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameUserResponseMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.OngoingEventResponse
import org.springframework.stereotype.Component

@Component
class AbilityNettyResponseMapper : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: GameData): Boolean =
        gameResponseMessage::class.java == ContainerGameData::class.java

    override fun accept(gameResponseMessage: GameData): Boolean = true

    override fun process(
        gameData: GameData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?
    ): AbilityNettyResponse {
        (gameData as AbilityGameData).let {
            return AbilityNettyResponse(
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
                ongoingEffects = gameData.visibleOngoingEffects.map {
                    OngoingEventResponse(it)
                }
            )
        }
    }
}