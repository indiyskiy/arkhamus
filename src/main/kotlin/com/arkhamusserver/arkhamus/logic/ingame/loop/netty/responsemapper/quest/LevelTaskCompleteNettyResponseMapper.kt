package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.*
import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.ShortTimeEventToResponseHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestProgressHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.quest.QuestRewardUtils
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.InBetweenEventHolder
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.RequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.quest.LevelTaskCompleteRequestProcessData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.responsemapper.NettyResponseMapper
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.view.dto.netty.request.NettyBaseRequestMessage
import com.arkhamusserver.arkhamus.view.dto.netty.response.mapCellsToResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.MyGameUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.OngoingEventResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.QuestInfoResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.quest.LevelTaskCompleteNettyResponse
import org.springframework.stereotype.Component

@Component
class LevelTaskCompleteNettyResponseMapper(
    private val otherGameUsersDataHandler: OtherGameUsersDataHandler,
    private val containersDataHandler: ContainerDataHandler,
    private val craftersDataHandler: CrafterDataHandler,
    private val questProgressHandler: QuestProgressHandler,
    private val rewardUtils: QuestRewardUtils,
    private val shortTimeEventToResponseHandler: ShortTimeEventToResponseHandler,
    private val doorDataHandler: DoorDataHandler,
    private val lanternDataHandler: LanternDataHandler,
    private val voteSpotInfoMapper: VoteSpotInfoMapper,
    private val userStatusMapper: UserInGameStatusMapper,
    private val altarsDataHandler: AltarsDataHandler,
) : NettyResponseMapper {
    override fun acceptClass(gameResponseMessage: RequestProcessData): Boolean =
        gameResponseMessage::class.java == LevelTaskCompleteRequestProcessData::class.java

    override fun accept(gameResponseMessage: RequestProcessData): Boolean = true

    override fun process(
        requestProcessData: RequestProcessData,
        nettyRequestMessage: NettyBaseRequestMessage,
        user: UserAccount,
        gameSession: GameSession?,
        userRole: UserOfGameSession?,
        inBetweenEventHolder: InBetweenEventHolder,
        globalGameData: GlobalGameData
    ): LevelTaskCompleteNettyResponse {
        (requestProcessData as LevelTaskCompleteRequestProcessData).let {
            val inGameUser = it.gameUser!!
            return LevelTaskCompleteNettyResponse(
                questInfo = QuestInfoResponse(
                    userQuest = requestProcessData.userQuestProgress?.let { process ->
                        questProgressHandler.mapQuestProgress(
                            requestProcessData.quest,
                            process
                        )
                    },
                    questDifficulty = requestProcessData.quest?.difficulty,
                    rewards = rewardUtils.mapRewards(inGameUser, requestProcessData.questRewards),
                    canAccept = requestProcessData.canAccept,
                    canDecline = requestProcessData.canDecline,
                    canFinish = requestProcessData.canFinish,
                    rightQuestGiverForAction = true
                ),
                tick = it.tick,
                userId = user.id!!,
                myGameUser = MyGameUserResponse(inGameUser, it.userQuest),
                otherGameUsers = otherGameUsersDataHandler.map(
                    myUser = inGameUser,
                    it.otherGameUsers,
                    globalGameData.levelGeometryData
                ),
                ongoingEvents = requestProcessData.visibleOngoingEvents.map { event ->
                    OngoingEventResponse(event)
                },
                shortTimeEvents = shortTimeEventToResponseHandler.filterAndMap(
                    globalGameData.shortTimeEvents,
                    inGameUser,
                    it.inZones,
                    globalGameData
                ),
                availableAbilities = requestProcessData.availableAbilities,
                ongoingCraftingProcess = requestProcessData.ongoingCraftingProcess,
                userInventory = requestProcessData.visibleItems.mapCellsToResponse(),
                containers = containersDataHandler.map(
                    inGameUser,
                    it.containers,
                    globalGameData.levelGeometryData
                ),
                crafters = craftersDataHandler.map(
                    inGameUser,
                    it.crafters,
                    globalGameData.levelGeometryData
                ),
                inZones = requestProcessData.inZones,
                clues = requestProcessData.clues,
                doors = doorDataHandler.map(
                    inGameUser,
                    globalGameData.doors,
                    globalGameData.levelGeometryData
                ),
                lanterns = lanternDataHandler.map(
                    inGameUser,
                    globalGameData.lanterns,
                    globalGameData.levelGeometryData
                ),
                questGivers = questProgressHandler.mapQuestGivers(
                    it.userQuest,
                    inGameUser,
                    globalGameData
                ),
                questSteps = questProgressHandler.mapSteps(
                    it.userQuest,
                    inGameUser,
                    globalGameData,
                ),
                easyVoteSpots = voteSpotInfoMapper.mapEasy(
                    inGameUser,
                    globalGameData.voteSpots,
                    globalGameData.levelGeometryData
                ),
                altars = altarsDataHandler.mapAltars(globalGameData.altars, it.gameUser, globalGameData),
                statuses = userStatusMapper.mapStatuses(it.gameUser, globalGameData),
            )
        }
    }


}