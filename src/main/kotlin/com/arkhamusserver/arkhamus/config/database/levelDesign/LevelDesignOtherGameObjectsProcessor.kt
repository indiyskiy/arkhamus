package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignContainerInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignCrafterInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignLanternInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignVoteSpotInfoProcessor
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.view.levelDesign.LevelFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignOtherGameObjectsProcessor(
    private val levelDesignContainerInfoProcessor: LevelDesignContainerInfoProcessor,
    private val levelDesignCrafterInfoProcessor: LevelDesignCrafterInfoProcessor,
    private val levelDesignLanternInfoProcessor: LevelDesignLanternInfoProcessor,
    private val levelDesignVoteSpotInfoProcessor: LevelDesignVoteSpotInfoProcessor,
) {
    fun processAllSortOfMapObjects(
        levelFromJson: LevelFromJson,
        savedLevel: Level
    ) {
        levelDesignContainerInfoProcessor.processContainers(levelFromJson.containers, savedLevel)
        levelDesignCrafterInfoProcessor.processCrafters(levelFromJson.crafters, savedLevel)
        levelDesignLanternInfoProcessor.processLanterns(levelFromJson.lanterns, savedLevel)
        levelDesignVoteSpotInfoProcessor.processVoteSpots(levelFromJson.votespots, savedLevel)
    }
}