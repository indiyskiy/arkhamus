package com.arkhamusserver.arkhamus.logic.ingame.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class QuestDifficultyLogic(
    private val geometryUtils: GeometryUtils
) {
    companion object {
        const val EASY = 113
        const val NORMAL = 135
        const val HARD = 150
        const val VERY_HARD = 197

        private val logger = LoggerFactory.getLogger(QuestDifficultyLogic::class.java)
    }

    fun recount(quests: List<Quest>) {
        val questsSorted = quests.map {
            questDistance(it) to it
        }.sortedBy { it.first }
        val difficulty = QuestDifficulty.values().associate { it.ordinal to it }
        val size = QuestDifficulty.values().size
        val numberPerType = questsSorted.size / size
        questsSorted.forEachIndexed { i, pair ->
            val number = i / numberPerType
            pair.second.dificulty = difficulty[number]!!
        }
        logger.info(
            "Quests recounted: ${
                questsSorted.joinToString { "${it.first} - ${it.second.dificulty}" }
            }")
    }

    fun recount(quest: Quest) {
        val totalDistance = questDistance(quest)
        val difficulty = difficultyByDistance(totalDistance)
        quest.dificulty = difficulty
    }

    private fun questDistance(quest: Quest): Double = if (quest.questSteps.isEmpty()) {
        geometryUtils.distance(quest.startQuestGiver, quest.endQuestGiver)
    } else {
        countTotalDistance(quest)
    }

    private fun difficultyByDistance(totalDistance: Double): QuestDifficulty {
        if (totalDistance <= EASY) {
            return QuestDifficulty.VERY_EASY
        }
        if (totalDistance <= NORMAL) {
            return QuestDifficulty.EASY
        }
        if (totalDistance <= HARD) {
            return QuestDifficulty.NORMAL
        }
        if (totalDistance <= VERY_HARD) {
            return QuestDifficulty.HARD
        }
        return QuestDifficulty.VERY_HARD
    }

    private fun countTotalDistance(quest: Quest): Double {
        val startDistance =
            geometryUtils.distance(quest.startQuestGiver, quest.questSteps.minBy { it.stepNumber }.levelTask)
        val lastDistance =
            geometryUtils.distance(quest.endQuestGiver, quest.questSteps.maxBy { it.stepNumber }.levelTask)
        if (quest.questSteps.size == 1) {
            return startDistance + lastDistance
        }
        val tasks = quest.questSteps.apply {
            this.sortedBy { it.stepNumber }
        }
        val inBetweenDistances = (0 until tasks.size - 1).sumOf { i ->
            geometryUtils.distance(tasks[i].levelTask, tasks[i + 1].levelTask)
        }
        return startDistance + inBetweenDistances + lastDistance
    }

}