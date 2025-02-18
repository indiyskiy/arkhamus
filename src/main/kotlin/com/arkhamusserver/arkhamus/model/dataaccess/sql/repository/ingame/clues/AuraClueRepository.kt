package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.AuraClue
import org.springframework.data.repository.CrudRepository

interface AuraClueRepository : CrudRepository<AuraClue, Long> {
    fun findByLevelId(levelId: Long): List<AuraClue>
}