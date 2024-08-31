package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool.Companion.MAX_TIME_NO_RESPONSES
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.forEach

@Component
class OneTickTryEndGameMaybeHandler(
    private val gameEndLogic: GameEndLogic,
    private val redisGameUserRepository: RedisGameUserRepository,
    private val madnessHandler: UserMadnessHandler,
) {

    @Transactional
    fun checkIfEnd(game: RedisGame, users: Collection<RedisGameUser>) {
        checkIfEverybodyMad(game, users)
        abandonIfAllLeave(game, users)
        markLeaversIfNoResponses(game, users)
    }

    private fun checkIfEverybodyMad(
        game: RedisGame,
        users: Collection<RedisGameUser>
    ) {
        if (users.size <= 1) {
            return
        }
        val notLeavers = users.filter { !it.leftTheGame }
        val notLeversNotCultists = notLeavers.filter { it.role != RoleTypeInGame.CULTIST }
        val notMadNotCultists = notLeversNotCultists.filter { !madnessHandler.isCompletelyMad(it) }
        if(notMadNotCultists.isEmpty()){
            gameEndLogic.endTheGame(game, users.associateBy { it.userId }, GameEndReason.EVERYBODY_MAD)
        }
    }

    private fun abandonIfAllLeave(game: RedisGame, users: Collection<RedisGameUser>) {
        if (users.all { it.leftTheGame }) {
            GameThreadPool.Companion.logger.info("all users ${users.joinToString { it.userId.toString() }} leave from the game ${game.id}, abandoning..")
            gameEndLogic.endTheGame(game, users.associateBy { it.userId }, GameEndReason.ABANDONED, 10_000)
        }
    }

    private fun markLeaversIfNoResponses(game: RedisGame, users: Collection<RedisGameUser>) {
        if (game.globalTimer - game.lastTimeSentResponse > MAX_TIME_NO_RESPONSES) {
            GameThreadPool.Companion.logger.info("no responses too long (${game.globalTimer} now, last was ${game.lastTimeSentResponse}) for game ${game.id}, marking all users as leavers")
            users.forEach { it.leftTheGame = true }
            redisGameUserRepository.saveAll(users)
        }
    }
}