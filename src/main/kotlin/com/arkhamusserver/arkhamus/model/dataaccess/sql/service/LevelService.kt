package com.arkhamusserver.arkhamus.model.dataaccess.sql.service

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import org.springframework.stereotype.Service

@Service
class LevelService(
    private val levelRepository: LevelRepository
) {
    fun latestByLevelIdAndVersion(levelId: Long): Level =
        levelRepository.findByLevelId(levelId).maxBy { it.version }
}