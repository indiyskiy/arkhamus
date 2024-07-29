package com.arkhamusserver.arkhamus.logic.ingame.quest

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import org.springframework.stereotype.Component

@Component
class LevelDifficultyLogic(
    private val geometryUtils: GeometryUtils
) {
    companion object {
        const val VERY_EASY = 0
        const val EASY = 100
        const val NORMAL = 150
        const val HARD = 200
        const val VERY_HARD = 250
    }

    fun recount(quest: Quest) {
        val totalDistance = if (quest.questSteps.isEmpty()) {
            geometryUtils.distance(quest.startQuestGiver, quest.endQuestGiver)
        } else {
            countTotalDistance(quest)
        }
        val difficulty = difficultyByDistance(totalDistance)
        quest.dificulty = difficulty
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