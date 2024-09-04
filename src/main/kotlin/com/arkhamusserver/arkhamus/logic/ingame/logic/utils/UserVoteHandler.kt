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
        val canVote = generalVoteHandler.userCanPossiblyVote(allUsers)
        val canVoteIdsSet = canVote.map { it.userId }.toSet()
        val votesStillRelevant = userVoteSpots.filter { it.userId in canVoteIdsSet }

        val (maxValue, maxVotes) = statistic(votesStillRelevant, voteSpot.availableUsers.toSet())
        val enoughVotes = maxValue >= (canVoteIdsSet.size / 2) + (canVoteIdsSet.size % 2)

        if (enoughVotes) {
            logger.info("looking for a quorum - enough votes")
            if (maxVotes.size > 1) {
                if (canVoteIdsSet.size == maxValue) {
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
    ): Pair<Int, Set<Long>> {
        val allVotes = votesStillRelevant.map { it.votesForUserIds }.flatten()
        val votesForAvailableUsers = allVotes.filter { it in availableUserIds }
        val statistic = votesForAvailableUsers.groupingBy { it }.eachCount()
        val maxValue = statistic.maxBy { it.value }.value
        val maxVotes = statistic.filter { it.value == maxValue }.keys
        return Pair(maxValue, maxVotes)
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