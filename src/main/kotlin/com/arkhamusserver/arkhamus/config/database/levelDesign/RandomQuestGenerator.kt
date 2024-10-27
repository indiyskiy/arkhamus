package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.QUESTS_ON_START
import com.arkhamusserver.arkhamus.logic.ingame.quest.LevelDifficultyLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.TextKeyRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestStepRepository
import com.arkhamusserver.arkhamus.model.database.entity.TextKey
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.LevelTask
import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.database.entity.game.QuestGiver
import com.arkhamusserver.arkhamus.model.database.entity.game.QuestStep
import com.arkhamusserver.arkhamus.model.enums.TextKeyType
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class RandomQuestGenerator(
    private val questRepository: QuestRepository,
    private val textKeyRepository: TextKeyRepository,
    private val questStepRepository: QuestStepRepository,
    private val levelDifficultyLogic: LevelDifficultyLogic,
) {

    companion object {
        private val random = Random(System.currentTimeMillis())
        private val logger: Logger = LoggerFactory.getLogger(RandomQuestGenerator::class.java)
    }

    fun generateRandomQuests(level: Level, questGivers: List<QuestGiver>, levelTasks: List<LevelTask>) {
        val hasOldQuests = questRepository.findAll().iterator().hasNext()
        if (hasOldQuests) {
            return
        }
        logger.info("processing quest for level ${level.id}")
        val quests = (0..QUESTS_ON_START * 4).map { number ->
            val randomQuestGiverStart = questGivers.random()
            val randomQuestGiverEnd = questGivers.random()

            val stepSize = random.nextInt(1, 2)
            val textKey = TextKey(type = TextKeyType.QUEST, value = "quest$number")
            val savedTextKey = textKeyRepository.save(textKey)
            val newQuest = Quest(
                id = null,
                level = level,
                questSteps = mutableListOf(),
                questState = QuestState.ACTIVE,
                name = "awesome quest $number",
                startQuestGiver = randomQuestGiverStart,
                endQuestGiver = randomQuestGiverEnd,
                textKey = savedTextKey
            )
            levelTasks.shuffled(random).take(stepSize).forEachIndexed { i, task ->
                val step = QuestStep(
                    id = null, stepNumber = i, quest = newQuest, levelTask = task
                )
                newQuest.addQuestStep(step)
            }

            logger.info("created quest: ${newQuest.name}")
            levelDifficultyLogic.recount(newQuest)
            questRepository.save(newQuest)
            questStepRepository.saveAll(newQuest.questSteps)
            newQuest
        }
        quests.groupBy { it.dificulty }.map { it.key to it.value.size }
    }
}