package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.SoundClue
import org.springframework.data.repository.CrudRepository

interface SoundClueRepository : CrudRepository<SoundClue, Long> {
    fun findByLevelId(levelId: Long): List<SoundClue>
}