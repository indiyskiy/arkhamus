package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.ritual

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.GodVoteSkipRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.ritual.GodVoteSkipNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.NettyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.OngoingEventResponse
import org.springframework.stereotype.Component

@Component
class GodVoteSkipNettyResponseMapper : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == GodVoteSkipRequestProcessData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder
    ): GodVoteSkipNettyResponse {
        (requestProcessData as GodVoteSkipRequestProcessData).let {
            return GodVoteSkipNettyResponse(
                executedSuccessfully = it.executedSuccessfully,
                firstTime = true,
                tick = it.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponse(it.gameUser!!),
                otherGameUsers = it.otherGameUsers.map { gameUser ->
                    NettyGameUserResponse(
                        id = gameUser.userId,
                        nickName = gameUser.nickName,
                        x = gameUser.x,
                        y = gameUser.y
                    )
                },
                ongoingEvents = requestProcessData.visibleOngoingEvents.map { event ->
                    OngoingEventResponse(event)
                },
                availableAbilities = requestProcessData.availableAbilities,
                ongoingCraftingProcess = requestProcessData.ongoingCraftingProcess,
                userInventory = requestProcessData.visibleItems,
                containers = requestProcessData.containers
            )
        }
    }
}