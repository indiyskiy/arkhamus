package com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.VoteSpot
import org.springframework.data.repository.CrudRepository


interface VoteSpotRepository : CrudRepository<VoteSpot, Long> {
    fun findByLevelId(levelId: Long): List<VoteSpot>
}