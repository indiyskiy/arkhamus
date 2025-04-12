package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.ShortTimeEventToResponseHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.HeartbeatRequestGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.HeartbeatNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.mapCellsToResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.OngoingEventResponse
import org.springframework.stereotype.Component

@Component
class HeartbeatNettyResponseMapper(
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
    private val shortTimeEventToResponseHandler: ShortTimeEventToResponseHandler,
    private val doorDataHandler: DoorDataHandler,
    private val lanternDataHandler: LanternDataHandler,
    private val voteSpotInfoMapper: VoteSpotInfoMapper,
    private val questProgressHandler: QuestProgressHandler,
    private val altarsDataHandler: AltarsDataHandler,
    private val userStatusMapper: UserInGameStatusMapper
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
                myGameUser = MyGameUserResponse(it.gameUser!!, it.userQuest),
                otherGameUsers = otherGameUsersDataHandler.map(
                    myUser = it.gameUser,
                    it.otherGameUsers,
                    globalGameData.levelGeometryData
                ),
                shortTimeEvents = shortTimeEventToResponseHandler.filterAndMap(
                    globalGameData.shortTimeEvents,
                    it.gameUser,
                    it.inZones,
                    globalGameData
                ),
                ongoingEvents = it.visibleOngoingEvents.map { event ->
                    OngoingEventResponse(event)
                },
                availableAbilities = it.availableAbilities,
                ongoingCraftingProcess = it.ongoingCraftingProcess,
                userInventory = it.visibleItems.mapCellsToResponse(),
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
                inZones = it.inZones,
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
                clues = it.clues,
                altars = altarsDataHandler.mapAltars(
                    globalGameData.altars,
                    it.gameUser,
                    globalGameData
                ),
                statuses = userStatusMapper.mapStatuses(it.gameUser, globalGameData),
            )
        }
    }

}