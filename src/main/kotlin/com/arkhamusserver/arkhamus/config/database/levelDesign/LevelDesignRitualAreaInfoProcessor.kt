package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.RitualAreaRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.RitualArea
import com.arkhamusserver.arkhamus.view.levelDesign.RitualAreaFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignRitualAreaInfoProcessor(
    private val ritualAreaRepository: RitualAreaRepository,
) {
    fun processRitualArea(
        ritualAreas: List<RitualAreaFromJson>,
        savedLevel: Level?
    ) {
        ritualAreas.first().let { ritualArea ->
            RitualArea(
                inGameId = ritualArea.id!!,
                radius = ritualArea.radius!!,
                x = ritualArea.x!!,
                y = ritualArea.y!!,
                z = ritualArea.z!!,
                level = savedLevel!!
            ).apply {
                ritualAreaRepository.save(this)
            }
        }
    }
}