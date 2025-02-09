package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.DistortionClue
import org.springframework.data.repository.CrudRepository

interface DistortionClueRepository : CrudRepository< DistortionClue, Long> {
    fun findByLevelId(levelId: Long): List< DistortionClue>
}