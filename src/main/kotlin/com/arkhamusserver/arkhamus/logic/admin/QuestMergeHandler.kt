package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelTask
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.QuestGiver
import com.arkhamusserver.arkhamus.view.dto.admin.AdminQuestDto
import org.springframework.stereotype.Component

@Component
class QuestMergeHandler {

    fun merge(
        quest: Quest,
        questDto: AdminQuestDto,
        allRelatedTasks: Map<Long, LevelTask>,
        questGivers: Map<Long, QuestGiver>
    ) {
        quest.name = questDto.name
        quest.questState = questDto.state
        quest.dificulty = questDto.difficulty
//        removeSteps(quest, questDto)
        changeSteps(quest, questDto, allRelatedTasks)
//        addSteps(quest, questDto, allRelatedTasks)
        sortSteps(quest)
        changeQuestGivers(quest, questDto, questGivers)
    }

    private fun changeQuestGivers(quest: Quest, questDto: AdminQuestDto, questGivers: Map<Long, QuestGiver>) {
        quest.startQuestGiver = questGivers[questDto.startQuestGiver.id]!!
        quest.endQuestGiver = questGivers[questDto.endQuestGiver.id]!!
    }

    fun sortSteps(quest: Quest) {
        quest.questSteps = quest.questSteps.sortedBy { it.stepNumber }.toMutableList()
        quest.questSteps.forEachIndexed { index, questStep ->
            questStep.stepNumber = index
        }
    }

    private fun changeSteps(quest: Quest, questDto: AdminQuestDto, allRelatedTasks: Map<Long, LevelTask>) {
        quest.questSteps.forEach { step ->
            val relatedDto = questDto.steps.first { it.id == step.id }
            step.stepNumber = relatedDto.number
            step.levelTask = allRelatedTasks[relatedDto.levelTask.id]!!
        }
    }

}