package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool.Companion.MAX_TIME_NO_RESPONSES
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.forEach

@Component
class OneTickTryEndGameMaybeHandler(
    private val gameEndLogic: GameEndLogic,
    private val madnessHandler: UserMadnessHandler,
) {

    @Transactional
    fun checkIfEnd(game: RedisGame, users: Collection<RedisGameUser>) {
        val mad = checkIfEverybodyMad(game, users)
        if (mad) {
            return
        }
        markLeaversIfNoResponses(game, users)
        abandonIfAllLeave(game, users)
    }

    private fun checkIfEverybodyMad(
        game: RedisGame,
        users: Collection<RedisGameUser>
    ): Boolean {
        if (users.size <= 1) {
            return false
        }
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
            gameEndLogic.endTheGame(game, users.associateBy { it.userId }, GameEndReason.ABANDONED, timeLeft = GlobalGameSettings.MINUTE_IN_MILLIS)
        }
    }

    private fun markLeaversIfNoResponses(game: RedisGame, users: Collection<RedisGameUser>) {
        if (game.globalTimer - game.lastTimeSentResponse > MAX_TIME_NO_RESPONSES) {
            users.forEach { it.leftTheGame = true }
        }
    }
}