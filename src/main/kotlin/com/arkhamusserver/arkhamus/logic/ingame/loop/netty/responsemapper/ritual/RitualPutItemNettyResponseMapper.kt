package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.ritual

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.ShortTimeEventToResponseHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.RitualMappingDataHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.ritual.RitualPutItemRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.mapCellsToResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.OngoingEventResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.ritual.RitualPutItemNettyResponse
import org.springframework.stereotype.Component

@Component
class RitualPutItemNettyResponseMapper(
    private val ritualMappingDataHandler: RitualMappingDataHandler,
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
    private val shortTimeEventToResponseHandler: ShortTimeEventToResponseHandler,
    private val doorDataHandler: DoorDataHandler,
    private val lanternDataHandler: LanternDataHandler,
    private val voteSpotInfoMapper: VoteSpotInfoMapper
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
                ritualGoingData = ritualMappingDataHandler.build(
                    requestProcessData.ritualEvent,
                    requestProcessData.altarHolder!!,
                    requestProcessData.usersInRitual,
                    requestProcessData.currentNotch,
                    requestProcessData.notches
                ),
                executedSuccessfully = it.executedSuccessfully,
                firstTime = true,
                tick = it.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponse(it.gameUser!!, it.userQuest),
                otherGameUsers = otherGameUsersDataHandler.map(
                    myUser = it.gameUser,
                    it.otherGameUsers,
                    globalGameData.levelGeometryData
                ),
                ongoingEvents = requestProcessData.visibleOngoingEvents.map { event ->
                    OngoingEventResponse(event)
                },
                shortTimeEvents = shortTimeEventToResponseHandler.filterAndMap(
                    globalGameData.shortTimeEvents,
                    it.gameUser,
                    it.inZones,
                    globalGameData
                ),
                availableAbilities = requestProcessData.availableAbilities,
                ongoingCraftingProcess = requestProcessData.ongoingCraftingProcess,
                userInventory = requestProcessData.visibleItems.mapCellsToResponse(),
                containers = containersDataHandler.map(
                    it.gameUser,
                    it.containers,
                    globalGameData.levelGeometryData
                ),
                crafters = craftersDataHandler.map(
                    it.gameUser,
                    it.crafters,
                    globalGameData.levelGeometryData
                ),
                inZones = requestProcessData.inZones,
                clues = requestProcessData.clues,
                doors = doorDataHandler.map(
                    it.gameUser,
                    globalGameData.doors,
                    globalGameData.levelGeometryData
                ),
                lanterns = lanternDataHandler.map(
                    it.gameUser,
                    globalGameData.lanterns,
                    globalGameData.levelGeometryData
                ),
                easyVoteSpots = voteSpotInfoMapper.mapEasy(
                    it.gameUser,
                    globalGameData.voteSpots,
                    globalGameData.levelGeometryData
                ),
            )
        }
    }
}