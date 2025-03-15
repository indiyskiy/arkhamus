package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.votespot

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.ShortTimeEventToResponseHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.banvote.VoteSpotCastRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.banvote.VoteSpotCastNettyResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.mapCellsToResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.OngoingEventResponse
import org.springframework.stereotype.Component

@Component
class VoteSpotCastNettyResponseMapper(
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
    private val shortTimeEventToResponseHandler: ShortTimeEventToResponseHandler,
    private val doorDataHandler: DoorDataHandler,
    private val lanternDataHandler: LanternDataHandler,
    private val voteSpotInfoMapper: VoteSpotInfoMapper,
    private val questProgressHandler: QuestProgressHandler,
) : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == VoteSpotCastRequestProcessData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder,
        globalGameData: GlobalGameData
    ): VoteSpotCastNettyResponse {
        (requestProcessData as VoteSpotCastRequestProcessData).let {
            return VoteSpotCastNettyResponse(
                successfullyVoted = it.successfullyVoted,
                targetUserBanned = it.targetUserBanned,
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
                lanterns = lanternDataHandler.map(
                    it.gameUser,
                    globalGameData.lanterns,
                    globalGameData.levelGeometryData
                ),
            )
        }
    }

}