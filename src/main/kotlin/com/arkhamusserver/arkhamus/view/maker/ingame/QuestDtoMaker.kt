package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.model.database.entity.game.LevelTask
import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.database.entity.game.QuestGiver
import com.arkhamusserver.arkhamus.model.database.entity.game.QuestStep
import com.arkhamusserver.arkhamus.view.dto.ingame.LevelTaskDto
import com.arkhamusserver.arkhamus.view.dto.ingame.QuestDto
import com.arkhamusserver.arkhamus.view.dto.ingame.QuestGiverDto
import com.arkhamusserver.arkhamus.view.dto.ingame.QuestStepDto
import org.springframework.stereotype.Component

@Component
class QuestDtoMaker {
    fun convert(quest: Quest): QuestDto {
        return QuestDto(
            id = quest.id!!,
            levelId = quest.level.id!!,
            name = quest.name,
            steps = quest.questSteps.map { step ->
                convert(step)
            },
            startQuestGiver = convert(quest.startQuestGiver),
            endQuestGiver = convert(quest.endQuestGiver),
        )
    }

    fun convert(step: QuestStep): QuestStepDto {
        return QuestStepDto(
            id = step.id!!, number = step.stepNumber, levelTask = convert(step.levelTask)
        )
    }

    fun convert(task: LevelTask): LevelTaskDto {
        return LevelTaskDto(
            id = task.id!!, inGameId = task.inGameId, name = task.name
        )
    }

    fun convert(npc: QuestGiver): QuestGiverDto {
        return QuestGiverDto(
            id = npc.id!!, inGameId = npc.inGameId, name = npc.name
        )
    }
}