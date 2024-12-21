package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.ScentClue
import org.springframework.data.repository.CrudRepository

interface ScentClueRepository : CrudRepository<ScentClue, Long> {
    fun findByLevelId(levelId: Long): List<ScentClue>
}