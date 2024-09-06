package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.ThresholdRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.Threshold
import com.arkhamusserver.arkhamus.view.levelDesign.ThresholdFromJson
import org.postgresql.geometric.PGpoint
import org.springframework.stereotype.Component

@Component
class LevelDesignThresholdInfoProcessor(
    private val thresholdRepository: ThresholdRepository
) {
    fun processThresholds(thresholds: List<ThresholdFromJson>, savedLevel: Level) {
        thresholds.forEach { threshold ->
            Threshold(
                point = PGpoint(threshold.x!!, threshold.y!!),
                inGameId = threshold.id!!,
                level = savedLevel,
                zoneId = threshold.zoneId!!
            ).apply {
                thresholdRepository.save(this)
            }
        }
    }
}