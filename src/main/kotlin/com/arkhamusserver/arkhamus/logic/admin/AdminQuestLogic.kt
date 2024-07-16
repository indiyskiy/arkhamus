package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelTaskRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.QuestStepRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.LevelTask
import com.arkhamusserver.arkhamus.model.database.entity.game.Quest
import com.arkhamusserver.arkhamus.model.database.entity.game.QuestStep
import com.arkhamusserver.arkhamus.view.dto.admin.AdminQuestDto
import com.arkhamusserver.arkhamus.view.dto.admin.LevelTaskDto
import com.arkhamusserver.arkhamus.view.dto.admin.QuestStepDto
import com.arkhamusserver.arkhamus.view.validator.utils.assertEquals
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class AdminQuestLogic(
    private val questRepository: QuestRepository,
    private val questStepRepository: QuestStepRepository,
    private val levelRepository: LevelRepository,
    private val levelTaskRepository: LevelTaskRepository,
    private val stepRepository: QuestStepRepository,
    private val questMergeHandler: QuestMergeHandler
) {

    fun get(questId: Long): AdminQuestDto {
        val quest = questRepository.findById(questId)
        return quest.get().toDto()
    }

    fun all(levelId: Long): List<AdminQuestDto> {
        return questRepository.findByLevelId(levelId).map { it.toDto() }
    }

    fun create(levelId: Long): AdminQuestDto {
        val level = levelRepository.findByLevelId(levelId).maxBy { it.version }
        val quest = newQuest(level)
        val saved = questRepository.save(quest)

        val levelTask = defaultLevelTask(quest.level.id!!)
        val questStep = QuestStep(
            stepNumber = 0,
            quest = quest,
            levelTask = levelTask
        )
        val savedStep = questStepRepository.save(questStep)
        quest.questSteps += savedStep
        return saved.toDto()
    }

    @Transactional
    fun save(questId: Long, questDto: AdminQuestDto): AdminQuestDto {
        val quest = questRepository.findById(questId).get()
        assertEquals(quest.id!!, questDto.id!!, "changing wrong quest", Quest::class.simpleName!!)
        val allRelatedTasks: List<LevelTask> = (quest.questSteps.map { it.levelTask }) +
                (questDto.steps.mapNotNull { it.levelTask?.id }.map { levelTaskRepository.findById(it).get() })
        val allRelatedTasksMap = allRelatedTasks.filter { it.id != null }.associateBy { it.id!! }
        questMergeHandler.merge(quest, questDto, allRelatedTasksMap)
        val saved = questRepository.save(quest)
        return saved.toDto()
    }

    fun addStep(questId: Long): AdminQuestDto {
        val quest = questRepository.findById(questId).get()
        val levelTask = defaultLevelTask(quest.level.id!!)
        quest.addQuestStep(newStep(quest, levelTask))
        val saved = questRepository.save(quest)
        return saved.toDto()
    }

    private fun defaultLevelTask(levelId: Long): LevelTask {
        return levelTaskRepository.findByLevelId(levelId).maxBy { it.id ?: 0 }
    }

    private fun newStep(quest: Quest, levelTask: LevelTask): QuestStep {
        val step = QuestStep(
            stepNumber = quest.questSteps.size,
            quest = quest,
            levelTask = levelTask
        )
        return stepRepository.save(step)
    }

    fun possibleTasks(levelId: Long): List<LevelTaskDto> {
        return levelTaskRepository.findByLevelId(levelId).map { it.toDto() }
    }

    private fun newQuest(level: Level): Quest = Quest(
        level = level
    )


    private fun Quest.toDto(): AdminQuestDto =
        AdminQuestDto(
            id = this.id!!,
            levelId = this.level.id!!,
            state = this.questState,
            name = this.name,
            steps = this.questSteps.map { it.toDto() }.sortedBy { it.number }.toMutableList(),
        )


    private fun LevelTask.toDto(): LevelTaskDto =
        LevelTaskDto(
            id = this.id!!,
            name = this.name
        )

    private fun QuestStep.toDto() = QuestStepDto(
        id = this.id!!,
        number = this.stepNumber,
        levelTask = this.levelTask.toDto()
    )

}