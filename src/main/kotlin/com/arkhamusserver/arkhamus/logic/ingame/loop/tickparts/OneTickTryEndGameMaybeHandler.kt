package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread.GameThreadPool.Companion.MAX_TIME_NO_RESPONSES
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameVoteSpot
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OneTickTryEndGameMaybeHandler(
    private val gameEndLogic: GameEndLogic,
    private val madnessHandler: UserMadnessHandler,
) {

    companion object {
        private val logger = LoggingUtils.getLogger<OneTickTryEndGameMaybeHandler>()
    }

    @Transactional
    fun checkIfEnd(game: InRamGame, users: Collection<InGameUser>, voteSpots: List<InGameVoteSpot>) {
        if (game.state == GameState.GAME_END_SCREEN.name ||
            game.state == GameState.FINISHED.name
        ) {
            return
        }
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
        game: InRamGame,
        users: Collection<InGameUser>,
        voteSpots: List<InGameVoteSpot>
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
        gameEndLogic.endTheGame(game, users.associateBy { it.inGameId() }, GameEndReason.CULTISTS_BANNED)
        return true
    }

    private fun singlePlayerGame(users: Collection<InGameUser>): Boolean {
        return users.size <= 1
    }

    private fun checkIfEverybodyMad(
        game: InRamGame,
        users: Collection<InGameUser>
    ): Boolean {
        if (singlePlayerGame(users)) return false
        val notLeavers = users.filter { !it.techData.leftTheGame }
        val notLeversNotCultists = notLeavers.filter { it.role != RoleTypeInGame.CULTIST }
        val notMadNotCultists = notLeversNotCultists.filter { !madnessHandler.isCompletelyMad(it) }
        if (notMadNotCultists.isEmpty()) {
            gameEndLogic.endTheGame(game, users.associateBy { it.inGameId() }, GameEndReason.EVERYBODY_MAD)
            return true
        }
        return false
    }

    private fun abandonIfAllLeave(game: InRamGame, users: Collection<InGameUser>) {
        if (users.all { it.techData.leftTheGame }) {
            logger.info("end the game - ABANDONED")
            gameEndLogic.endTheGame(
                game,
                users.associateBy { it.inGameId() },
                GameEndReason.ABANDONED,
                timeLeft = GlobalGameSettings.MINUTE_IN_MILLIS
            )
        }
    }

    private fun markLeaversIfNoResponses(game: InRamGame, users: Collection<InGameUser>) {
        if (game.globalTimer - game.lastTimeSentResponse > MAX_TIME_NO_RESPONSES) {
            users.forEach {
                it.techData.leftTheGame = true
            }
            logger.info(
                "no requests for ${game.gameId} - all users left - marked them - ${
                    users.joinToString {
                        it.inGameId().toString()
                    }
                }"
            )
        }
    }
}