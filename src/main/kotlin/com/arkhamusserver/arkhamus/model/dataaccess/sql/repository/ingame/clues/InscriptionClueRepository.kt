package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.InscriptionClue
import org.springframework.data.repository.CrudRepository

interface InscriptionClueRepository : CrudRepository<InscriptionClue, Long> {
    fun findByLevelId(levelId: Long): List<InscriptionClue>
}