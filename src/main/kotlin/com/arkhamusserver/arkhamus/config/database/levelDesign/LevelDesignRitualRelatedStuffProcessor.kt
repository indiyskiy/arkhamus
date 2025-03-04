package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignAltarInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignRitualAreaInfoProcessor
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.view.levelDesign.LevelFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignRitualRelatedStuffProcessor(
    private val levelDesignAltarInfoProcessor: LevelDesignAltarInfoProcessor,
    private val levelDesignRitualAreaInfoProcessor: LevelDesignRitualAreaInfoProcessor,
) {
    fun processRitualRelatedStuff(
        levelFromJson: LevelFromJson,
        savedLevel: Level
    ) {
        levelDesignAltarInfoProcessor.processAltars(levelFromJson.altars, savedLevel)
        levelDesignRitualAreaInfoProcessor.processRitualArea(levelFromJson.ritualZones, savedLevel)
    }
}