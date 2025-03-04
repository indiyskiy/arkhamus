package com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LanternRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Lantern
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.view.levelDesign.LanternFromJson
import org.springframework.stereotype.Component

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
                interactionRadius = lantern.interactionRadius,
                x = lantern.x!!,
                y = lantern.y!!,
                z = lantern.z!!,
                level = savedLevel!!
            ).apply {
                lanternRepository.save(this)
            }
        }
    }
}