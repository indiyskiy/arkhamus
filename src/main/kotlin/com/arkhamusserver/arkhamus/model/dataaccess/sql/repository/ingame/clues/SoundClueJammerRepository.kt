package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues.SoundClueJammer
import org.springframework.data.repository.CrudRepository

interface SoundClueJammerRepository : CrudRepository<SoundClueJammer, Long> {
    fun findByLevelId(levelId: Long): List<SoundClueJammer>
}