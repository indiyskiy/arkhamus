package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.DoorRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Door
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.view.levelDesign.DoorFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignDoorInfoProcessor(
    private val doorRepository: DoorRepository
) {
    fun processDoors(thresholds: List<DoorFromJson>, savedLevel: Level) {
        thresholds.forEach { threshold ->
            Door(
                x = threshold.x!!,
                y = threshold.y!!,
                z = threshold.z!!,
                inGameId = threshold.id!!,
                level = savedLevel,
                zoneId = threshold.zoneId!!
            ).apply {
                doorRepository.save(this)
            }
        }
    }
}