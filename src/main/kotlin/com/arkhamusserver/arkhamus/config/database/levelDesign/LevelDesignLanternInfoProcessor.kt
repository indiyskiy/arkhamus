package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LanternRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Lantern
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.view.levelDesign.LanternFromJson
import org.postgresql.geometric.PGpoint
import org.springframework.stereotype.Component
import kotlin.collections.forEach

@Component
class LevelDesignLanternInfoProcessor(
    private val lanternRepository: LanternRepository
) {
    fun processLanterns(
        containers: List<LanternFromJson>,
        savedLevel: Level?
    ) {
        containers.forEach { lantern ->
            Lantern(
                inGameId = lantern.id!!,
                lightRange = lantern.lightRange,
                point = PGpoint(lantern.x!!, lantern.y!!),
                level = savedLevel!!
            ).apply {
                lanternRepository.save(this)
            }
        }
    }
}