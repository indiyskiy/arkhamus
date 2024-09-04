package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisUserVoteSpotRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisVoteSpotRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.VoteSpotRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.VoteSpot
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisUserVoteSpot
import com.arkhamusserver.arkhamus.model.redis.RedisVoteSpot
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import kotlin.Long

@Component
class GameStartVoteSpotLogic(
    private val voteSpotRepository: VoteSpotRepository,
    private val redisVoteSpotRepository: RedisVoteSpotRepository,
    private val redisUserVoteSpotRepository: RedisUserVoteSpotRepository
) {

    companion object {
        private val DEFAULT_ITEM = Item.VOTE_TOKEN
    }

    fun createVoteSpots(levelId: Long, game: GameSession, users: List<RedisGameUser>) {
        val voteSpots = voteSpotRepository.findByLevelId(levelId)
        voteSpots.forEach { voteSpot ->
            val redisVoteSpot = createVoteSpot(voteSpot, game, users)
            users.forEach { user ->
                createUserVoteSpot(redisVoteSpot, game, user)
            }
        }
    }

    private fun createUserVoteSpot(
        voteSpot: RedisVoteSpot,
        game: GameSession,
        user: RedisGameUser
    ) {
        redisUserVoteSpotRepository.save(
            RedisUserVoteSpot(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = game.id!!,
                voteSpotId = voteSpot.voteSpotId,
                userId = user.userId,
                votesForUserIds = mutableListOf()
            )
        )
    }

    private fun createVoteSpot(
        voteSpot: VoteSpot,
        game: GameSession,
        allUsers: List<RedisGameUser>
    ) =
        redisVoteSpotRepository.save(
            RedisVoteSpot(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = game.id!!,
                voteSpotId = voteSpot.inGameId,
                x = voteSpot.point.x,
                y = voteSpot.point.y,
                interactionRadius = voteSpot.interactionRadius,
                costValue = 1,
                costItem = DEFAULT_ITEM.id,
                bannedUsers = mutableListOf(),
                availableUsers = allUsers.map { it.userId }.toMutableList(),
            )
        )

}