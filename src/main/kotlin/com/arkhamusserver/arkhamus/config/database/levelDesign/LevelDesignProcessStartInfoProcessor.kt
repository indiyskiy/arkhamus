package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.StartMarkerRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.StartMarker
import com.arkhamusserver.arkhamus.view.levelDesign.JsonStartMarker
import org.springframework.stereotype.Component

@Component
class LevelDesignProcessStartInfoProcessor(
    private val startMarkerRepository: StartMarkerRepository,
) {
    fun processStartMarkers(
        containers: List<JsonStartMarker>,
        savedLevel: Level?
    ) {
        containers.forEach { startMarker ->
            StartMarker(
                x = startMarker.x!!,
                y = startMarker.y!!,
                z = startMarker.z!!,
                level = savedLevel!!
            ).apply {
                startMarkerRepository.save(this)
            }
        }
    }
}