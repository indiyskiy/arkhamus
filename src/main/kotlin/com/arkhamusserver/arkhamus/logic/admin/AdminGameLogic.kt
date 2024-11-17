package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.logic.UserSkinLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameActivityRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.GameActivity
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.view.dto.admin.*
import com.arkhamusserver.arkhamus.view.maker.GameSessionDtoMaker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AdminGameLogic(
    private val gameSessionRepository: GameSessionRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val gameSessionDtoMaker: GameSessionDtoMaker,
    private val userSkinLogic: UserSkinLogic,
    private val activityRepository: GameActivityRepository,
) {

    companion object {
        const val SCREEN_ZOOM = 10
        private val logger = LoggerFactory.getLogger(AdminGameLogic::class.java)
    }


    fun all(): List<AdminGameSessionDto> {
        return gameSessionRepository
            .findAll()
            .sortedBy { it.creationTimestamp?.time ?: 0 }
            .map { game ->
                gameSessionDto(game)
            }
    }

    fun allForUser(userId: Long): AdminUserGameDataDto {
        val userGameSessions = userOfGameSessionRepository
            .findByUserAccountId(userId)
            .sortedBy { it.gameSession.creationTimestamp?.time ?: 0 }
        val games = userGameSessions.map {
            AdminUserGameSessionDto().apply {
                this.classInGame = it.classInGame
                this.roleInGame = it.roleInGame
                this.winOrLoose = it.won?.let { if (it) "win" else "loose" } ?: "who knows"
                this.gameSession = adminGameSessionDto(it.gameSession)
            }
        }
        val losses = userGameSessions.filter { it.won != null && it.won == false }.size
        val wins = userGameSessions.filter { it.won != null && it.won == true }.size
        val winrate = if (wins == 0) {
            0
        } else {
            100 * wins / (wins + losses)
        }
        return AdminUserGameDataDto(
            games = games,
            losses = losses,
            wins = wins,
            winrate = winrate
        )
    }

    fun statisticWinRate(): GameStatisticHolder {
        val allReasons = GameEndReason.values()
        val allGames = gameSessionRepository.findByState(GameState.FINISHED)
        val statistic = allGames
            .groupBy { it.gameEndReason }
            .map { it.key to it.value.size }
            .toMap()
        return GameStatisticHolder(
            labelList = allReasons.map { it.name },
            dataList = allReasons.map { statistic[it] ?: 0 }
        )
    }

    fun game(gameId: Long): AdminGameSessionDto {
        val game = gameSessionRepository.findById(gameId).orElse(null)
        return adminGameSessionDto(game)
    }

    @Transactional
    fun getGameActivities(
        gameId: Long,
        userIds: List<Long>,
        activityTypes: List<ActivityType>
    ): GameActivitiesDto {
        val colors = NiceColor.values().associateBy { it.ordinal }
        val colorSize = colors.size

        val game = gameSessionRepository.findById(gameId).orElse(null)
        val activities = activityRepository.findByGameSessionId(gameId).filter {
            it.userOfGameSession.userAccount.id in userIds &&
                    it.activityType in activityTypes
        }
        val activityDtos = activities.map { activity ->
            dto(colors, activity, colorSize)
        }
        val level = game.gameSessionSettings.level!!
        logger.info("created ${activityDtos.size} activityDtos")
        return GameActivitiesDto(
            gameId = gameId,
            userIds = userIds,
            activityTypes = activityTypes,
            activities = activityDtos,
            levelId = level.id!!,
            height = level.levelHeight.toInt() * SCREEN_ZOOM,
            width = level.levelWidth.toInt() * SCREEN_ZOOM
        )
    }

    private fun dto(
        colors: Map<Int, NiceColor>,
        activity: GameActivity,
        colorSize: Int
    ): GameActivityDto {
        val color = colors[activity.activityType.ordinal % colorSize]!!
        return GameActivityDto(
            type = activity.activityType,
            color = color,
            points = listOf(
                PointDto(
                    activity.x.toFloat() * AdminLevelPreviewLogic.Companion.SCREEN_ZOOM - 5,
                    activity.z.toFloat() * AdminLevelPreviewLogic.Companion.SCREEN_ZOOM - 5,
                    color
                ),
                PointDto(
                    (activity.x * AdminLevelPreviewLogic.Companion.SCREEN_ZOOM + 5).toFloat(),
                    (activity.z * AdminLevelPreviewLogic.Companion.SCREEN_ZOOM - 5).toFloat(),
                    color
                ),
                PointDto(
                    (activity.x * AdminLevelPreviewLogic.Companion.SCREEN_ZOOM + 5).toFloat(),
                    (activity.z * AdminLevelPreviewLogic.Companion.SCREEN_ZOOM + 5).toFloat(),
                    color
                ),
                PointDto(
                    (activity.x * AdminLevelPreviewLogic.Companion.SCREEN_ZOOM - 5).toFloat(),
                    (activity.z * AdminLevelPreviewLogic.Companion.SCREEN_ZOOM + 5).toFloat(),
                    color
                )
            ),
        )
    }

    @Transactional
    fun getAllUsers(gameId: Long): List<SimpleUserDto> =
        gameSessionRepository.findById(gameId).orElse(null)?.let {
            it.usersOfGameSession.map {
                SimpleUserDto(
                    it.userAccount.id!!,
                    it.userAccount.nickName,
                )
            }
        } ?: emptyList()

    private fun adminGameSessionDto(game: GameSession?): AdminGameSessionDto {
        return game?.let {
            val skins = userSkinLogic.allSkinsOf(game)
            gameSessionDtoMaker.toDtoAsAdmin(game, skins)
        } ?: AdminGameSessionDto()
    }

    private fun gameSessionDto(game: GameSession): AdminGameSessionDto {
        val skins = userSkinLogic.allSkinsOf(game)
        return gameSessionDtoMaker.toDtoAsAdmin(game, skins)
    }

}