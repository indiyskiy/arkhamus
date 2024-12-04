package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.ThresholdRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Threshold
import com.arkhamusserver.arkhamus.model.enums.ingame.ThresholdType
import com.arkhamusserver.arkhamus.view.levelDesign.ThresholdFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignThresholdInfoProcessor(
    private val thresholdRepository: ThresholdRepository
) {
    fun processThresholds(
        thresholds: List<ThresholdFromJson>,
        thresholdType: ThresholdType,
        savedLevel: Level
    ) {
        thresholds.forEach { threshold ->
            Threshold(
                x = threshold.x!!,
                y = threshold.y!!,
                z = threshold.z!!,
                inGameId = threshold.id!!,
                level = savedLevel,
                zoneId = threshold.zoneId!!,
                type = thresholdType
            ).apply {
                thresholdRepository.save(this)
            }
        }
    }
}