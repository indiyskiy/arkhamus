package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameActivityRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.view.dto.admin.*
import org.springframework.stereotype.Component
import kotlin.math.roundToLong

@Component
class AdminLevelInfoLogic(
    private val levelRepository: LevelRepository,
    private val gameSessionRepository: GameSessionRepository,
    private val activityRepository: GameActivityRepository,
) {
    companion object {
        val relatedTypes = setOf(
            ActivityType.ABILITY_CASTED,
            ActivityType.BAN_SPOT_PAYED,
            ActivityType.BAN_SPOT_VOTE_CASTED,
            ActivityType.BAN_SPOT_USER_BANED,
            ActivityType.CRAFT_STARTED,
            ActivityType.ALTAR_VOTE_STARTED,
            ActivityType.ALTAR_VOTE_CASTED,
            ActivityType.CLUE_CREATED,
            ActivityType.QUEST_ACCEPTED,
            ActivityType.QUEST_DECLINED,
            ActivityType.QUEST_COMPLETE,
        )
    }

    fun all(): List<AdminGameLevelInfoDto> {
        return levelRepository
            .findAll()
            .groupBy { it.levelId }
            .map { it.value.maxBy { it.version } }
            .map { mapLevel(it) }
    }

    private fun mapLevel(it: Level) = AdminGameLevelInfoDto(
        it.levelId,
        it.version,
        it.state,
        it.levelHeight,
        it.levelWidth
    )

    fun info(levelId: Long): AdminGameLevelInfoDto {
        return levelRepository.findByLevelId(levelId).maxBy { it.version }.let { mapLevel(it) }
    }

    fun statistic(levelId: Long): AdminGameLevelStatisticDto {
        val games = gameSessionRepository.findByGameSessionSettingsLevelId(levelId).filter {
            it.state == GameState.FINISHED
        }.filter {
            it.gameType in setOf(GameType.CUSTOM, GameType.DEFAULT)
        }
        val (winRateByClassElementDtos, winRateByReasonElementDtos) = countWinRate(games)
        val averageGameLength = averageGameLengthSeconds(games).toStringDate()
        val averageGameLengthByGameEndReason = averageGameLengthByGameEndReason(games).map {
            GameTimeByReasonElementDto(
                reason = it.first,
                value = it.second
            )
        }
        val activitiesStatistic = countStatistic(games)
        return AdminGameLevelStatisticDto(
            averageGameLength,
            averageGameLengthByGameEndReason,
            winRateByClassElementDtos,
            winRateByReasonElementDtos,
            activitiesStatistic
        )
    }

    private fun countStatistic(games: List<GameSession>): List<ActivityStatisticDto> {
        val gameIds = games.map { it.id!! }.toSet()
        if (gameIds.isEmpty()) return emptyList()
        val activities = activityRepository.findByGameSessionIdInAndActivityTypeIn(
            gameIds,
            relatedTypes
        ).groupBy { it.activityType }
        return activities.map { (type, activities) ->
            ActivityStatisticDto(type, 1.0 * activities.size / gameIds.size)
        }
    }

    private fun averageGameLengthByGameEndReason(sessions: List<GameSession>): List<Pair<GameEndReason?, String>> {
        return sessions.groupBy {
            it.gameEndReason
        }.map {
            it.key to averageGameLengthSeconds(it.value).toStringDate()
        }
    }

    private fun averageGameLengthSeconds(sessions: List<GameSession>): Double {
        return sessions.map {
            it.finishedTimestamp to it.startedTimestamp
        }.filter {
            it.first != null && it.second != null
        }.map {
            (it.first!!.getTime() / 1000) - (it.second!!.getTime() / 1000)
        }.takeIf { it.isNotEmpty() }?.average() ?: 0.0
    }

    private fun countWinRate(games: List<GameSession>): Pair<List<WinRateByClassElementDto>, List<WinRateByReasonElementDto>> {
        val stat = games.groupBy {
            it.gameEndReason
        }.map {
            it.key to it.value.size
        }
        val total = stat.sumOf { it.second }
        if (total == 0) return emptyList<WinRateByClassElementDto>() to emptyList<WinRateByReasonElementDto>()
        val winRateByReason = stat.map {
            WinRateByReasonElementDto(
                reason = it.first,
                value = 1.0 * it.second / total * 100
            )
        }
        val winRateByClass = winRateByReason.groupBy {
            it.reason?.toRole()
        }.map {
            it.key to it.value.sumOf { it.value }
        }.map {
            WinRateByClassElementDto(
                role = it.first,
                value = it.second
            )
        }
        return winRateByClass to winRateByReason
    }

    private fun GameEndReason?.toRole(): RoleTypeInGame? =
        when (this) {
            GameEndReason.GOD_AWAKEN -> RoleTypeInGame.CULTIST
            GameEndReason.EVERYBODY_MAD -> RoleTypeInGame.CULTIST
            GameEndReason.CULTISTS_BANNED -> RoleTypeInGame.INVESTIGATOR
            GameEndReason.RITUAL_SUCCESS -> RoleTypeInGame.INVESTIGATOR
            GameEndReason.ABANDONED -> null
            null -> null
        }

    private fun Double.toStringDate(): String {
        val hours: Long = (this / 3600).roundToLong() // 1 hour = 3600 seconds
        val minutes: Long = ((this % 3600) / 60).roundToLong() // Remaining minutes
        val seconds: Long = (this % 60).roundToLong() // Remaining seconds
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}





