package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.StartMarkerRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.StartMarker
import com.arkhamusserver.arkhamus.view.levelDesign.StartMarkerFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignProcessStartInfoProcessor(
    private val startMarkerRepository: StartMarkerRepository,
) {
    fun processStartMarkers(
        containers: List<StartMarkerFromJson>,
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