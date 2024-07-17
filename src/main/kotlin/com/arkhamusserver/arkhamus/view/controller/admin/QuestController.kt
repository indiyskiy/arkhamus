package com.arkhamusserver.arkhamus.view.controller.admin

import com.arkhamusserver.arkhamus.logic.admin.AdminQuestLogic
import com.arkhamusserver.arkhamus.view.dto.admin.AdminQuestDto
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/level/{levelId}/quest")
class QuestController(
    private val adminQuestLogic: AdminQuestLogic
) {

    companion object {
        val logger = LoggerFactory.getLogger(QuestController::class.java)!!
    }

    @PostMapping
    fun createQuest(
        @PathVariable levelId: Long,
    ): AdminQuestDto {
        val createdQuest: AdminQuestDto = adminQuestLogic.create(levelId)
        return createdQuest
    }

    @PostMapping("{questId}")
    fun saveQuest(
        @PathVariable levelId: Long,
        @PathVariable questId: Long,
        @RequestBody quest: AdminQuestDto,
    ): AdminQuestDto {
        val savedQuest: AdminQuestDto = adminQuestLogic.save(questId, quest)
        return savedQuest
    }

    @PostMapping("{questId}/addStep")
    fun addStep(
        @PathVariable levelId: Long,
        @PathVariable questId: Long,
    ): AdminQuestDto {
        val createdQuest: AdminQuestDto = adminQuestLogic.addStep(questId)
        return createdQuest
    }

    @PostMapping("{questId}/removeStep/{stepId}")
    fun removeStep(
        @PathVariable levelId: Long,
        @PathVariable questId: Long,
        @PathVariable stepId: Long,
    ): AdminQuestDto {
        logger.info("removing step $stepId")
        val createdQuest: AdminQuestDto = adminQuestLogic.removeStep(questId, stepId)
        logger.info("removed")
        return createdQuest
    }
}