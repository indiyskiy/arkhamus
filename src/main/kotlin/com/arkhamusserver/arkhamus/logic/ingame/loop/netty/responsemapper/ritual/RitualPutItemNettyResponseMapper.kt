package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.OtherGameUsersDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.RitualGoingDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualPutItemRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.OngoingEventResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.ritual.RitualPutItemNettyResponse
import org.springframework.stereotype.Component

@Component
class RitualPutItemNettyResponseMapper(
    private val ritualGoingDataHandler: RitualGoingDataHandler,
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler
) : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == RitualPutItemRequestProcessData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder,
        globalGameData: GlobalGameData
    ): RitualPutItemNettyResponse {
        (requestProcessData as RitualPutItemRequestProcessData).let {
            return RitualPutItemNettyResponse(
                itemId = requestProcessData.item?.id,
                itemNumber = requestProcessData.itemNumber,
                ritualGoingData = ritualGoingDataHandler.build(
                    requestProcessData.ritualEvent,
                    requestProcessData.altarHolder!!,
                    requestProcessData.usersInRitual
                ),
                executedSuccessfully = it.executedSuccessfully,
                firstTime = true,
                tick = it.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponse(it.gameUser!!),
                otherGameUsers = otherGameUsersDataHandler.map(
                    myUser = it.gameUser,
                    it.otherGameUsers,
                    globalGameData.levelGeometryData
                ),
                ongoingEvents = requestProcessData.visibleOngoingEvents.map { event ->
                    OngoingEventResponse(event)
                },
                availableAbilities = requestProcessData.availableAbilities,
                ongoingCraftingProcess = requestProcessData.ongoingCraftingProcess,
                userInventory = requestProcessData.visibleItems,
                containers = requestProcessData.containers,
                inZones = requestProcessData.inZones,
                clues = requestProcessData.clues
            )
        }
    }
}