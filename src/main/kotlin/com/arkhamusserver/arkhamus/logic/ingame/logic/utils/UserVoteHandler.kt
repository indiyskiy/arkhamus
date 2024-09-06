package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserVoteSpotRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisVoteSpotRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.CantVoteReason
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
    private val madnessHandler: UserMadnessHandler
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
            CantVoteReason.MAD.takeIf { madnessHandler.isCompletelyMad(votingUser) },
            CantVoteReason.CANT_PAY.takeIf {
                inventoryHandler.userHaveItems(
                    votingUser,
                    voteSpot?.costItem,
                    voteSpot?.costValue ?: 0
                )
            },
            CantVoteReason.BANNED.takeIf { voteSpot?.bannedUsers?.contains(votingUser.userId) == true },
        ).filterNotNull()
    }

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
    fun gotQuorum(
        allUsers: Collection<RedisGameUser>,
        voteSpot: RedisVoteSpot,
        userVoteSpots: List<RedisUserVoteSpot>,
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
                banUser(voteSpot, userToBan)
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

    private fun votesToBan(size: Int): Int = (size / 2) + 1

    private fun usersCanPossiblyVote(
        users: Collection<RedisGameUser>,
        voteSpot: RedisVoteSpot,
    ): List<RedisGameUser> {
        return users.filter {
            cantVoteReasons(it, voteSpot).any { it in CANT_VOTE_AT_ALL }
        }
    }

    private fun banUser(
        voteSpot: RedisVoteSpot,
        userId: Long
    ) {
        logger.debug("ban user $userId")
        voteSpot.bannedUsers += userId
        voteSpot.availableUsers -= userId
        val value = voteSpot.costValue
        if (value != null && value > 0) {
            voteSpot.costValue = value + 1
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

}