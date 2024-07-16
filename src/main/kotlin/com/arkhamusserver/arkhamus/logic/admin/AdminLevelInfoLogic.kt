package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameLevelInfoDto
import org.springframework.stereotype.Component

@Component
class AdminLevelInfoLogic(
    private val levelRepository: LevelRepository,
) {
    fun all(): List<AdminGameLevelInfoDto> {
        return levelRepository
            .findAll()
            .groupBy { it.levelId }
            .map { it.value.maxBy { it.version } }
            .map {
                mapLevel(it)
            }
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
}