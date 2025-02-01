package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.CorruptionClue
import org.springframework.data.repository.CrudRepository

interface CorruptionClueRepository : CrudRepository<CorruptionClue, Long> {
    fun findByLevelId(levelId: Long): List<CorruptionClue>
}