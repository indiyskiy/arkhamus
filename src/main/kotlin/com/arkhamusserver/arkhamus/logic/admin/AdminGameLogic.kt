package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.logic.UserSkinLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameSessionDto
import com.arkhamusserver.arkhamus.view.dto.admin.AdminUserGameSessionDto
import com.arkhamusserver.arkhamus.view.dto.admin.GameStatisticHolder
import com.arkhamusserver.arkhamus.view.maker.GameSessionDtoMaker
import org.springframework.stereotype.Component

@Component
class AdminGameLogic(
    private val gameSessionRepository: GameSessionRepository,
    private val userOfGameSessionRepository: UserOfGameSessionRepository,
    private val gameSessionDtoMaker: GameSessionDtoMaker,
    private val userSkinLogic: UserSkinLogic,
) {
    fun all(): List<AdminGameSessionDto> {
        return gameSessionRepository
            .findAll()
            .sortedBy { it.creationTimestamp?.time ?: 0 }
            .map { game ->
                gameSessionDto(game)
            }
    }

    fun allForUser(userId: Long): List<AdminUserGameSessionDto> {
        val userGameSessions = userOfGameSessionRepository
            .findByUserAccountId(userId)
            .sortedBy { it.gameSession.creationTimestamp?.time ?: 0 }
        return userGameSessions.map {
            AdminUserGameSessionDto().apply {
                this.classInGame = it.classInGame
                this.roleInGame = it.roleInGame
                this.gameSession = adminGameSessionDto(it.gameSession)
            }
        }
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