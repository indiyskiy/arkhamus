package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignLevelTaskInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.LevelDesignQuestGiverInfoProcessor
import com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors.RandomQuestGenerator
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.CREATE_TEST_QUESTS
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.levelDesign.LevelFromJson
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LevelDesignQuestsRelatedStuffProcessor(
    private val levelDesignQuestGiverInfoProcessor: LevelDesignQuestGiverInfoProcessor,
    private val randomQuestGenerator: RandomQuestGenerator,
    private val levelDesignLevelTaskInfoProcessor: LevelDesignLevelTaskInfoProcessor,
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

        if (CREATE_TEST_QUESTS) {
            logger.info("creating random quests")
            randomQuestGenerator.generateRandomQuests(savedLevel, questGivers, levelTasks)
        }
    }
}