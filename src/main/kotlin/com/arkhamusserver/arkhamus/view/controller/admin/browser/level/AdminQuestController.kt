package com.arkhamusserver.arkhamus.view.controller.admin.browser.level

import com.arkhamusserver.arkhamus.logic.admin.AdminQuestLogic
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.arkhamusserver.arkhamus.view.dto.admin.AdminQuestDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/level/{levelId}/quest")
class AdminQuestController(
    private val adminQuestLogic: AdminQuestLogic
) {

    companion object {
        private val logger = LoggingUtils.getLogger<AdminQuestController>()
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
        logger.info("save by rest controller")
        val savedQuest: AdminQuestDto = adminQuestLogic.update(questId, quest)
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