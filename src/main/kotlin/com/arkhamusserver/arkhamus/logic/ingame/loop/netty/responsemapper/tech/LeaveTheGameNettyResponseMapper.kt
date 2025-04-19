package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.tech

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.ShortTimeEventToResponseHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.tech.LeaveTheGameRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.mapCellsToResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.OngoingEventResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.tech.LeaveTheGameNettyResponse
import org.springframework.stereotype.Component

@Component
class LeaveTheGameNettyResponseMapper(
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
    private val shortTimeEventToResponseHandler: ShortTimeEventToResponseHandler,
    private val doorDataHandler: DoorDataHandler,
    private val lanternDataHandler: LanternDataHandler,
    private val voteSpotInfoMapper: VoteSpotInfoMapper,
    private val questProgressHandler: QuestProgressHandler,
    private val userStatusMapper: UserInGameStatusMapper,
    private val altarsDataHandler: AltarsDataHandler,
) : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == LeaveTheGameRequestGameData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder,
        globalGameData: GlobalGameData
    ): LeaveTheGameNettyResponse {
        (requestProcessData as LeaveTheGameRequestGameData).let {
            return LeaveTheGameNettyResponse(
                leftTheGame = it.canLeaveTheGame,
                tick = it.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponse(it.gameUser!!, emptyList()),
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
                    myUser = it.gameUser,
                    containers = it.containers,
                    levelGeometryData = globalGameData.levelGeometryData
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
                questGivers = questProgressHandler.mapQuestGivers(
                    it.userQuest,
                    it.gameUser,
                    globalGameData
                ),
                questSteps = questProgressHandler.mapSteps(
                    it.userQuest,
                    it.gameUser,
                    globalGameData,
                ),
                easyVoteSpots = voteSpotInfoMapper.mapEasy(
                    it.gameUser,
                    globalGameData.voteSpots,
                    globalGameData.levelGeometryData
                ),
                altars = altarsDataHandler.mapAltars(globalGameData.altars, it.gameUser, globalGameData),
                statuses = userStatusMapper.mapStatuses(it.gameUser, globalGameData),
            )
        }
    }

}