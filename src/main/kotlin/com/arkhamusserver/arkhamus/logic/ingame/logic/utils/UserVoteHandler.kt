package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GameDataLevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisDoorRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserVoteSpotRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisVoteSpotRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.CantVoteReason
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.get

@Component
class UserVoteHandler(
    private val userVoteSpotRepository: RedisUserVoteSpotRepository,
    private val voteSpotRepository: RedisVoteSpotRepository,
    private val inventoryHandler: InventoryHandler,
    private val madnessHandler: UserMadnessHandler,
    private val teleportHandler: TeleportHandler,
    private val zonesHandler: ZonesHandler,
    private val geometryUtils: GeometryUtils,
    private val redisDoorRepository: RedisDoorRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserVoteHandler::class.java)
        private val CANT_VOTE_AT_ALL = setOf(CantVoteReason.MAD, CantVoteReason.BANNED)
    }

    fun cantVoteReasons(
        votingUser: RedisGameUser,
        voteSpot: RedisVoteSpot?,
    ): List<CantVoteReason> {
        return listOf(
            mad(votingUser),
            cantPay(votingUser, voteSpot),
            isUserBannedFromVoteSpot(voteSpot, votingUser),
        ).filterNotNull()
    }

    private fun isUserBannedFromVoteSpot(
        voteSpot: RedisVoteSpot?,
        votingUser: RedisGameUser
    ): CantVoteReason? = CantVoteReason.BANNED.takeIf { voteSpot?.bannedUsers?.contains(votingUser.userId) == true }

    private fun cantPay(
        votingUser: RedisGameUser,
        voteSpot: RedisVoteSpot?
    ): CantVoteReason? = CantVoteReason.CANT_PAY.takeIf {
        !inventoryHandler.userHaveItems(
            votingUser,
            voteSpot?.costItem,
            voteSpot?.costValue ?: 0
        )
    }

    private fun mad(votingUser: RedisGameUser): CantVoteReason? =
        CantVoteReason.MAD.takeIf { madnessHandler.isCompletelyMad(votingUser) }

    @Transactional
    fun castVote(
        currentUserVoteSpot: RedisUserVoteSpot,
        targetUser: RedisGameUser,
        globalGameData: GlobalGameData,
        requestDataHolder: NettyTickRequestMessageDataHolder,
        voteSpot: RedisVoteSpot
    ) {
        currentUserVoteSpot.votesForUserIds = (currentUserVoteSpot.votesForUserIds + targetUser.userId)
            .toMutableList()
        userVoteSpotRepository.save(currentUserVoteSpot)
        inventoryHandler.consumeItems(
            user = globalGameData.users[requestDataHolder.userAccount.id]!!,
            item = voteSpot.costItem!!.toItem(),
            number = voteSpot.costValue!!
        )
    }

    @Transactional
    fun applyBanMaybe(
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
                    reset(userVoteSpots)
                }
                return null
            } else {
                val userToBan = maxVotes.first()
                banUser(
                    voteSpot,
                    allUsers.first { userToBan == it.userId },
                    globalGameData
                )
                reset(userVoteSpots)
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
        voteSpot: RedisVoteSpot,
        user: RedisGameUser,
        globalGameData: GlobalGameData
    ) {
        val userId = user.userId
        logger.debug("ban user $userId")
        voteSpot.bannedUsers += userId
        voteSpot.availableUsers -= userId
        val value = voteSpot.costValue
        if (value != null && value > 0) {
            voteSpot.costValue = value + 1
        }
        if (user.inRelatedZone(globalGameData.levelGeometryData.zones, voteSpot.zoneId)) {
            val pointToTeleport = geometryUtils.nearestPoint(user, globalGameData.thresholdsByZoneId[voteSpot.zoneId])
            pointToTeleport?.let {
                teleportHandler.forceTeleport(
                    game = globalGameData.game,
                    user = user,
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

        logger.debug("ban user $userId - done")
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

    private fun reset(userVoteSpots: List<RedisUserVoteSpot>) {
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
