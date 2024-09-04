package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.NettyTickRequestMessageDataHolder
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserVoteSpotRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisVoteSpotRepository
import com.arkhamusserver.arkhamus.model.redis.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.get

@Component
class UserVoteHandler(
    private val generalVoteHandler: GeneralVoteHandler,
    private val userVoteSpotRepository: RedisUserVoteSpotRepository,
    private val voteSpotRepository: RedisVoteSpotRepository,
    private val inventoryHandler: InventoryHandler,
) {
    companion object {
        var logger: Logger = LoggerFactory.getLogger(UserVoteHandler::class.java)
    }

    @Transactional
    fun castVote(
        currentUserVoteSpot: RedisUserVoteSpot,
        targetUser: RedisGameUser,
        globalGameData: GlobalGameData,
        requestDataHolder: NettyTickRequestMessageDataHolder,
        voteSpot: RedisVoteSpot
    ) {
        logger.info("old vote state ${currentUserVoteSpot.votesForUserIds.joinToString(", ")}")
        currentUserVoteSpot.votesForUserIds = (currentUserVoteSpot.votesForUserIds + targetUser.userId)
            .toMutableList()
        logger.info("new vote state ${currentUserVoteSpot.votesForUserIds.joinToString(", ")}")
        userVoteSpotRepository.save(currentUserVoteSpot)
        logger.info("also consuming item ${voteSpot.costItem} - ${voteSpot.costValue}")
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
        logger.info("looking for a quorum")
        val allUsersCanVoteList = generalVoteHandler.usersCanPossiblyVote(allUsers)
        logger.info("users can possibly vote: ${allUsersCanVoteList.joinToString(",") { it.userId.toString() }}")
        val usersCanVoteIdsSet = allUsersCanVoteList.map { it.userId }.toSet()
        logger.info("users can possibly vote ids: ${usersCanVoteIdsSet.joinToString(",") { it.toString() }}")
        val votesStillRelevant = userVoteSpots.filter { it.userId in usersCanVoteIdsSet }
        logger.info("user-votes still relevant: ${votesStillRelevant.joinToString(",") { it.userId.toString() }}")

        val (maxValue, maxVotes) = statistic(votesStillRelevant, voteSpot.availableUsers.toSet())
        if (maxValue == null || maxVotes == null) {
            return null
        }
        val enoughVotes = maxValue >= (usersCanVoteIdsSet.size / 2) + (usersCanVoteIdsSet.size % 2)

        if (enoughVotes) {
            logger.info("looking for a quorum - enough votes")
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
        logger.info("looking for a quorum - not enough votes")
        return null
    }

    private fun banUser(
        voteSpot: RedisVoteSpot,
        userId: Long
    ) {
        logger.info("ban user $userId")
        voteSpot.bannedUsers += userId
        voteSpot.availableUsers -= userId
        val value = voteSpot.costValue
        if (value != null && value > 0) {
            voteSpot.costValue = value + 1
        }
        logger.info("ban user $userId - done")
        voteSpotRepository.save(voteSpot)
    }

    private fun statistic(
        votesStillRelevant: List<RedisUserVoteSpot>,
        availableUserIds: Set<Long>
    ): Pair<Int?, Set<Long>?> {
        logger.info("counting statistic")
        val allVotes: List<Long> = votesStillRelevant.map { it.votesForUserIds }.flatten()
        logger.info("all votes for user: ${allVotes.joinToString(", ") { it.toString() }}")
        val votesForAvailableUsers = allVotes.filter { it in availableUserIds }
        logger.info("all votes for available users: ${votesForAvailableUsers.joinToString(", ") { it.toString() }}")
        val statistic = votesForAvailableUsers.groupingBy { it }.eachCount()
        logger.info("statistic: ${statistic.toList().joinToString(", ") { "${it.first} - ${it.second}" }}")
        val maxValue = statistic.maxByOrNull { it.value }?.value
        logger.info("maxValue: $maxValue")
        val userIdsWithMaxVotes = maxValue?.let { maxValueNBotNull -> statistic.filter { it.value == maxValueNBotNull }.keys }
        logger.info("userIdsWithMaxVotes: ${userIdsWithMaxVotes?.joinToString(", ") { it.toString() }}")
        return Pair(maxValue, userIdsWithMaxVotes)
    }

    private fun reset(userVoteSpots: List<RedisUserVoteSpot>) {
        logger.info("reset votes list")
        userVoteSpots.forEach {
            it.votesForUserIds = mutableListOf()
        }
        userVoteSpotRepository.saveAll(userVoteSpots)
        logger.info("reset votes list - done")
    }
}