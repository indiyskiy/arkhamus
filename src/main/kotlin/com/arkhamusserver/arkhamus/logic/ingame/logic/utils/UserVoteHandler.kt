package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GameDataLevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameDoorRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameUserVoteSpotRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameVoteSpotRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.*
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameUserVoteSpot
import com.arkhamusserver.arkhamus.model.ingame.InGameVoteSpot
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserVoteHandler(
    private val userVoteSpotRepository: InGameUserVoteSpotRepository,
    private val voteSpotRepository: InGameVoteSpotRepository,
    private val madnessHandler: UserMadnessHandler,
    private val teleportHandler: TeleportHandler,
    private val zonesHandler: ZonesHandler,
    private val geometryUtils: GeometryUtils,
    private val inGameDoorRepository: InGameDoorRepository,
    private val activityHandler: ActivityHandler,
) {
    companion object {
        private val CANT_VOTE_AT_ALL = setOf(CantVoteReason.MAD, CantVoteReason.BANNED)
    }

    fun cantVoteReasons(
        votingUser: InGameUser,
        voteSpot: InGameVoteSpot?,
    ): List<CantVoteReason> {
        return listOfNotNull(
            mad(votingUser),
            mustPay(voteSpot),
            isUserBannedFromVoteSpot(voteSpot, votingUser),
        )
    }

    private fun isUserBannedFromVoteSpot(
        voteSpot: InGameVoteSpot?,
        votingUser: InGameUser
    ): CantVoteReason? = CantVoteReason.BANNED.takeIf { voteSpot?.bannedUsers?.contains(votingUser.inGameId()) == true }

    private fun mustPay(
        voteSpot: InGameVoteSpot?
    ) = CantVoteReason.MUST_PAY.takeIf {
        voteSpot?.voteSpotState == VoteSpotState.WAITING_FOR_PAYMENT
    }

    private fun mad(votingUser: InGameUser): CantVoteReason? =
        CantVoteReason.MAD.takeIf { madnessHandler.isCompletelyMad(votingUser) }


    fun canVote(
        currentUserVoteSpot: InGameUserVoteSpot,
        targetUser: InGameUser,
        voteSpot: InGameVoteSpot,
    ): Boolean {
        if (cantVoteReasons(targetUser, voteSpot).isNotEmpty()) return false
        if (voteSpot.bannedUsers.contains(targetUser.inGameId())) return false
        if (voteSpot.bannedUsers.any { it == targetUser.inGameId() }) return false
        if (voteSpot.availableUsers.none { it == targetUser.inGameId() }) return false
        if (currentUserVoteSpot.votesForUserIds.any { it == targetUser.inGameId() }) return false
        return true
    }

    @Transactional
    fun castVote(
        currentUser: InGameUser,
        currentUserVoteSpot: InGameUserVoteSpot,
        targetUser: InGameUser,
        users: Collection<InGameUser>,
        voteSpot: InGameVoteSpot,
        allUserVoteSpots: List<InGameUserVoteSpot>,
        globalGameData: GlobalGameData,
    ): InGameUser? {
        currentUserVoteSpot.votesForUserIds = (currentUserVoteSpot.votesForUserIds + targetUser.inGameId())
            .toMutableList()
        userVoteSpotRepository.save(currentUserVoteSpot)
        activityHandler.addUserWithTargetActivity(
            globalGameData.game.inGameId(),
            ActivityType.BAN_SPOT_VOTE_CASTED,
            currentUser,
            globalGameData.game.globalTimer,
            GameObjectType.VOTE_SPOT,
            voteSpot,
            targetUser.inGameId()
        )
        return applyBanMaybe(
            currentUser,
            users,
            voteSpot,
            allUserVoteSpots,
            globalGameData
        )
    }

    @Transactional
    fun applyBanMaybe(
        currentUser: InGameUser,
        allUsers: Collection<InGameUser>,
        voteSpot: InGameVoteSpot,
        userVoteSpots: List<InGameUserVoteSpot>,
        globalGameData: GlobalGameData,
    ): InGameUser? {
        val allUsersCanVoteList = usersCanPossiblyVote(allUsers, voteSpot)
        val usersCanVoteIdsSet = allUsersCanVoteList.map { it.inGameId() }.toSet()
        val votesStillRelevant = userVoteSpots.filter { it.userId in usersCanVoteIdsSet }

        val (maxValue, maxVotes) = statistic(votesStillRelevant, voteSpot.availableUsers.toSet())
        if (maxValue == null || maxVotes == null) {
            return null
        }
        val enoughVotes = maxValue >= votesToBan(usersCanVoteIdsSet.size)

        if (enoughVotes) {
            if (maxVotes.size > 1) {
                if (usersCanVoteIdsSet.size == maxValue) {
                    reset(voteSpot, userVoteSpots)
                }
                return null
            } else {
                val userToBan = maxVotes.first()
                banUser(
                    currentUser,
                    voteSpot,
                    allUsers.first { userToBan == it.inGameId() },
                    globalGameData
                )
                reset(voteSpot, userVoteSpots)
                return allUsers.first { it.inGameId() == userToBan }
            }
        }
        return null
    }

    fun votesToBan(
        allUsers: Collection<InGameUser>,
        voteSpot: InGameVoteSpot,
    ): Int {
        val allUsersCanVoteList = usersCanPossiblyVote(allUsers, voteSpot)
        return votesToBan(allUsersCanVoteList.size)
    }

    fun getCanCallForVote(
        voteSpot: InGameVoteSpot?,
        ongoingEvents: List<OngoingEvent>,
        user: InGameUser
    ): Boolean = voteSpot != null &&
            callForVoteEventInProgress(ongoingEvents) &&
            user.callToArms > 0

    private fun callForVoteEventInProgress(ongoingEvents: List<OngoingEvent>): Boolean = !ongoingEvents.any {
        it.event.type == InGameTimeEventType.CALL_FOR_BAN_VOTE &&
                it.event.state == InGameTimeEventState.ACTIVE
    }

    private fun votesToBan(size: Int): Int = (size / 2) + 1

    private fun usersCanPossiblyVote(
        users: Collection<InGameUser>,
        voteSpot: InGameVoteSpot,
    ): List<InGameUser> {
        return users.filter {
            !cantVoteReasons(it, voteSpot).any { it in CANT_VOTE_AT_ALL }
        }
    }

    private fun banUser(
        currentUser: InGameUser,
        voteSpot: InGameVoteSpot,
        userToBan: InGameUser,
        globalGameData: GlobalGameData
    ) {
        val userId = userToBan.inGameId()
        voteSpot.bannedUsers += userId
        voteSpot.availableUsers -= userId
        if (userToBan.inRelatedZone(globalGameData.levelGeometryData.zones, voteSpot.zoneId)) {
            val pointToTeleport =
                geometryUtils.nearestPoint(userToBan, globalGameData.thresholds.filter {
                    it.zoneId == voteSpot.zoneId && it.type == ThresholdType.BAN
                })
            pointToTeleport?.let {
                teleportHandler.forceTeleport(
                    game = globalGameData.game,
                    user = userToBan,
                    point = it
                )
            }
        }
        val doors = globalGameData.doorsByZoneId[voteSpot.zoneId]
        doors?.let {
            it.forEach { door ->
                door.closedForUsers += userId
            }
            inGameDoorRepository.saveAll(it)
        }
        activityHandler.addUserWithTargetActivity(
            globalGameData.game.inGameId(),
            ActivityType.BAN_SPOT_USER_BANED,
            currentUser,
            globalGameData.game.globalTimer,
            GameObjectType.VOTE_SPOT,
            voteSpot,
            userToBan.inGameId()
        )
        voteSpotRepository.save(voteSpot)
    }

    private fun statistic(
        votesStillRelevant: List<InGameUserVoteSpot>,
        availableUserIds: Set<Long>
    ): Pair<Int?, Set<Long>?> {
        val allVotes: List<Long> = votesStillRelevant.map { it.votesForUserIds }.flatten()
        val votesForAvailableUsers = allVotes.filter { it in availableUserIds }
        val statistic = votesForAvailableUsers.groupingBy { it }.eachCount()
        val maxValue = statistic.maxByOrNull { it.value }?.value
        val userIdsWithMaxVotes =
            maxValue?.let { maxValueNBotNull -> statistic.filter { it.value == maxValueNBotNull }.keys }
        return Pair(maxValue, userIdsWithMaxVotes)
    }

    private fun reset(
        voteSpot: InGameVoteSpot,
        userVoteSpots: List<InGameUserVoteSpot>
    ) {
        voteSpot.voteSpotState = VoteSpotState.WAITING_FOR_PAYMENT
        voteSpotRepository.save(voteSpot)

        userVoteSpots.forEach {
            it.votesForUserIds = mutableListOf()
        }
        userVoteSpotRepository.saveAll(userVoteSpots)
    }

    private fun InGameUser.inRelatedZone(
        zones: List<GameDataLevelZone>,
        zoneId: Long
    ): Boolean {
        val zone = zones.firstOrNull { it.zoneId == zoneId }
        return zone?.let {
            zonesHandler.inZone(this, zone)
        } == true
    }
}
