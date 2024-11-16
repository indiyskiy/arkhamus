package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Altar
import org.springframework.data.repository.CrudRepository


interface AltarRepository : CrudRepository<Altar, Long> {
    fun findByLevelId(levelId: Long): List<Altar>
}