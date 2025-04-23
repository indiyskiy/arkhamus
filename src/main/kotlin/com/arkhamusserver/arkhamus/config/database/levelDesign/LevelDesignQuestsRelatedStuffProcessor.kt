package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignLevelTaskInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignQuestGiverInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.RandomQuestGenerator
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.levelDesign.LevelFromJson
import org.springframework.stereotype.Component

@Component
class LevelDesignQuestsRelatedStuffProcessor(
    private val levelDesignQuestGiverInfoProcessor: LevelDesignQuestGiverInfoProcessor,
    private val randomQuestGenerator: RandomQuestGenerator,
    private val levelDesignLevelTaskInfoProcessor: LevelDesignLevelTaskInfoProcessor,
    private val globalGameSettings: GlobalGameSettings,
) {

    companion object {
        private val logger = LoggingUtils.getLogger<LevelDesignQuestsRelatedStuffProcessor>()
    }

    fun generateQuestRelatedStuff(
        levelFromJson: LevelFromJson,
        savedLevel: Level
    ) {
        val questGivers =
            levelDesignQuestGiverInfoProcessor.processQuestGiverFromJson(levelFromJson.questGivers, savedLevel)
        val levelTasks =
            levelDesignLevelTaskInfoProcessor.processLevelTasksFromJson(levelFromJson.levelTasks, savedLevel)

        if (globalGameSettings.createTestQuests) {
            LoggingUtils.withContext(
                eventType = LoggingUtils.EVENT_OUTER_GAME_SYSTEM
            ) {
                logger.info("creating random quests")
            }
            randomQuestGenerator.generateRandomQuests(savedLevel, questGivers, levelTasks)
        }
    }
}
