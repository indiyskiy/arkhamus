package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ZonesHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GameDataLevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisDoorRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserVoteSpotRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisVoteSpotRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.CantVoteReason
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.VoteSpotState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisUserVoteSpot
import com.arkhamusserver.arkhamus.model.redis.RedisVoteSpot
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserVoteHandler(
    private val userVoteSpotRepository: RedisUserVoteSpotRepository,
    private val voteSpotRepository: RedisVoteSpotRepository,
    private val madnessHandler: UserMadnessHandler,
    private val teleportHandler: TeleportHandler,
    private val zonesHandler: ZonesHandler,
    private val geometryUtils: GeometryUtils,
    private val redisDoorRepository: RedisDoorRepository,
    private val activityHandler: ActivityHandler,
) {
    companion object {
        private val CANT_VOTE_AT_ALL = setOf(CantVoteReason.MAD, CantVoteReason.BANNED)
    }

    fun cantVoteReasons(
        votingUser: RedisGameUser,
        voteSpot: RedisVoteSpot?,
    ): List<CantVoteReason> {
        return listOfNotNull(
            mad(votingUser),
            mustPay(voteSpot),
            isUserBannedFromVoteSpot(voteSpot, votingUser),
        )
    }

    private fun isUserBannedFromVoteSpot(
        voteSpot: RedisVoteSpot?,
        votingUser: RedisGameUser
    ): CantVoteReason? = CantVoteReason.BANNED.takeIf { voteSpot?.bannedUsers?.contains(votingUser.userId) == true }

    private fun mustPay(
        voteSpot: RedisVoteSpot?
    ) = CantVoteReason.MUST_PAY.takeIf {
        voteSpot?.voteSpotState == VoteSpotState.WAITING_FOR_PAYMENT
    }

    private fun mad(votingUser: RedisGameUser): CantVoteReason? =
        CantVoteReason.MAD.takeIf { madnessHandler.isCompletelyMad(votingUser) }


    fun canVote(
        currentUserVoteSpot: RedisUserVoteSpot,
        targetUser: RedisGameUser,
        voteSpot: RedisVoteSpot,
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
        currentUser: RedisGameUser,
        currentUserVoteSpot: RedisUserVoteSpot,
        targetUser: RedisGameUser,
        users: Collection<RedisGameUser>,
        voteSpot: RedisVoteSpot,
        allUserVoteSpots: List<RedisUserVoteSpot>,
        globalGameData: GlobalGameData,
    ): RedisGameUser? {
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
        currentUser: RedisGameUser,
        allUsers: Collection<RedisGameUser>,
        voteSpot: RedisVoteSpot,
        userVoteSpots: List<RedisUserVoteSpot>,
        globalGameData: GlobalGameData,
    ): RedisGameUser? {
        val allUsersCanVoteList = usersCanPossiblyVote(allUsers, voteSpot)
        val usersCanVoteIdsSet = allUsersCanVoteList.map { it.userId }.toSet()
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
                return allUsers.first { it.userId == userToBan }
            }
        }
        return null
    }

    fun votesToBan(
        allUsers: Collection<RedisGameUser>,
        voteSpot: RedisVoteSpot,
    ): Int {
        val allUsersCanVoteList = usersCanPossiblyVote(allUsers, voteSpot)
        return votesToBan(allUsersCanVoteList.size)
    }

    fun getCanCallForVote(
        voteSpot: RedisVoteSpot?,
        ongoingEvents: List<OngoingEvent>,
        user: RedisGameUser
    ): Boolean = voteSpot != null &&
            callForVoteEventInProgress(ongoingEvents) &&
            user.callToArms > 0

    private fun callForVoteEventInProgress(ongoingEvents: List<OngoingEvent>): Boolean = !ongoingEvents.any {
        it.event.type == RedisTimeEventType.CALL_FOR_BAN_VOTE &&
                it.event.state == RedisTimeEventState.ACTIVE
    }

    private fun votesToBan(size: Int): Int = (size / 2) + 1

    private fun usersCanPossiblyVote(
        users: Collection<RedisGameUser>,
        voteSpot: RedisVoteSpot,
    ): List<RedisGameUser> {
        return users.filter {
            !cantVoteReasons(it, voteSpot).any { it in CANT_VOTE_AT_ALL }
        }
    }

    private fun banUser(
        currentUser: RedisGameUser,
        voteSpot: RedisVoteSpot,
        userToBan: RedisGameUser,
        globalGameData: GlobalGameData
    ) {
        val userId = userToBan.userId
        voteSpot.bannedUsers += userId
        voteSpot.availableUsers -= userId
        if (userToBan.inRelatedZone(globalGameData.levelGeometryData.zones, voteSpot.zoneId)) {
            val pointToTeleport =
                geometryUtils.nearestPoint(userToBan, globalGameData.thresholdsByZoneId[voteSpot.zoneId])
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
            redisDoorRepository.saveAll(it)
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
        votesStillRelevant: List<RedisUserVoteSpot>,
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
        voteSpot: RedisVoteSpot,
        userVoteSpots: List<RedisUserVoteSpot>
    ) {
        voteSpot.voteSpotState = VoteSpotState.WAITING_FOR_PAYMENT
        voteSpotRepository.save(voteSpot)

        userVoteSpots.forEach {
            it.votesForUserIds = mutableListOf()
        }
        userVoteSpotRepository.saveAll(userVoteSpots)
    }

    private fun RedisGameUser.inRelatedZone(
        zones: List<GameDataLevelZone>,
        zoneId: Long
    ): Boolean {
        val zone = zones.firstOrNull { it.zoneId == zoneId }
        return zone?.let {
            zonesHandler.inZone(this, zone)
        } == true
    }
}
