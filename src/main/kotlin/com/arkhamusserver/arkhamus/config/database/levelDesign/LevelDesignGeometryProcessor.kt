package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignDoorInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignProcessStartInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignThresholdInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignVisibilityProcessor
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.enums.ingame.ThresholdType
import com.arkhamusserver.arkhamus.view.levelDesign.LevelFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignGeometryProcessor(
    private val levelDesignProcessStartInfoProcessor: LevelDesignProcessStartInfoProcessor,
    private val levelDesignThresholdInfoProcessor: LevelDesignThresholdInfoProcessor,
    private val levelDesignDoorInfoProcessor: LevelDesignDoorInfoProcessor,
    private val levelDesignVisibilityProcessor: LevelDesignVisibilityProcessor,
) {
    fun processGeometry(
        levelFromJson: LevelFromJson,
        savedLevel: Level
    ) {
        levelDesignProcessStartInfoProcessor.processStartMarkers(levelFromJson.startMarkers, savedLevel)
        levelDesignThresholdInfoProcessor.processThresholds(
            levelFromJson.ritualThresholds,
            ThresholdType.RITUAL,
            savedLevel
        )
        levelDesignThresholdInfoProcessor.processThresholds(
            levelFromJson.banThresholds,
            ThresholdType.BAN,
            savedLevel
        )
        levelDesignDoorInfoProcessor.processDoors(levelFromJson.doors, savedLevel)
        levelDesignVisibilityProcessor.processVisibilityObjects(levelFromJson, savedLevel)
    }
}