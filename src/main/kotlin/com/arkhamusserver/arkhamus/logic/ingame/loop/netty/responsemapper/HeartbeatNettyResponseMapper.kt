package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.OtherGameUsersDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.HeartbeatRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.HeartbeatNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.OngoingEventResponse
import org.springframework.stereotype.Component

@Component
class HeartbeatNettyResponseMapper(
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
) : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == HeartbeatRequestGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder,
        globalGameData: GlobalGameData
    ): HeartbeatNettyResponse {
        (requestProcessData as HeartbeatRequestGameData).let {
            return HeartbeatNettyResponse(
                tick = it.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponse(it.gameUser!!),
                otherGameUsers = otherGameUsersDataHandler.map(
                    myUser = it.gameUser,
                    it.otherGameUsers,
                    globalGameData.levelGeometryData
                ),
                ongoingEvents = it.visibleOngoingEvents.map { event ->
                    OngoingEventResponse(event)
                },
                availableAbilities = it.availableAbilities,
                ongoingCraftingProcess = it.ongoingCraftingProcess,
                userInventory = it.visibleItems,
                containers = it.containers,
                inZones = it.inZones,
                clues = it.clues
            )
        }
    }

}