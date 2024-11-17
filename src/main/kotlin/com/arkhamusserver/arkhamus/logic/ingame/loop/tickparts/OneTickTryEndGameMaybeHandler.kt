package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool.Companion.MAX_TIME_NO_RESPONSES
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisVoteSpot
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OneTickTryEndGameMaybeHandler(
    private val gameEndLogic: GameEndLogic,
    private val madnessHandler: UserMadnessHandler,
) {

    companion object {
       private val logger = LoggerFactory.getLogger(OneTickTryEndGameMaybeHandler::class.java)
    }

    @Transactional
    fun checkIfEnd(game: RedisGame, users: Collection<RedisGameUser>, voteSpots: List<RedisVoteSpot>) {
        val mad = checkIfEverybodyMad(game, users)
        if (mad) {
            return
        }
        val allCultistsBanned = checkIfAllCultistsBanned(game, users, voteSpots)
        if (allCultistsBanned) {
            return
        }
        markLeaversIfNoResponses(game, users)
        abandonIfAllLeave(game, users)
    }

    private fun checkIfAllCultistsBanned(
        game: RedisGame,
        users: Collection<RedisGameUser>,
        voteSpots: List<RedisVoteSpot>
    ): Boolean {
        if (singlePlayerGame(users)) return false
        val allCultists = users.filter { it.role == RoleTypeInGame.CULTIST }
        val allCultistsBanned = allCultists.all { cultist ->
            voteSpots.all { voteSpot ->
                cultist.inGameId() in voteSpot.bannedUsers
            }
        }
        if (!allCultistsBanned) return false
        val noOneElseBaned = voteSpots.all { voteSpot ->
            allCultists.size == voteSpot.bannedUsers.size
        }
        if (!noOneElseBaned) return false
        gameEndLogic.endTheGame(game, users.associateBy { it.userId }, GameEndReason.CULTISTS_BANNED)
        return true
    }

    private fun singlePlayerGame(users: Collection<RedisGameUser>): Boolean {
        if (users.size <= 1) {
            return true
        }
        return false
    }

    private fun checkIfEverybodyMad(
        game: RedisGame,
        users: Collection<RedisGameUser>
    ): Boolean {
        if (singlePlayerGame(users)) return false
        val notLeavers = users.filter { !it.leftTheGame }
        val notLeversNotCultists = notLeavers.filter { it.role != RoleTypeInGame.CULTIST }
        val notMadNotCultists = notLeversNotCultists.filter { !madnessHandler.isCompletelyMad(it) }
        if (notMadNotCultists.isEmpty()) {
            gameEndLogic.endTheGame(game, users.associateBy { it.userId }, GameEndReason.EVERYBODY_MAD)
            return true
        }
        return false
    }

    private fun abandonIfAllLeave(game: RedisGame, users: Collection<RedisGameUser>) {
        if (users.all { it.leftTheGame }) {
            logger.info("end the gamer - ABANDONED")
            gameEndLogic.endTheGame(
                game,
                users.associateBy { it.userId },
                GameEndReason.ABANDONED,
                timeLeft = GlobalGameSettings.MINUTE_IN_MILLIS
            )
        }
    }

    private fun markLeaversIfNoResponses(game: RedisGame, users: Collection<RedisGameUser>) {
        if (game.globalTimer - game.lastTimeSentResponse > MAX_TIME_NO_RESPONSES) {
            users.forEach { it.leftTheGame = true }
            logger.info(
                "no requests for ${game.gameId} - all users left - marked them - ${users.joinToString{ it.inGameId().toString() }}"
            )
        }
    }
}