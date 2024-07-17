package com.arkhamusserver.arkhamus.view.controller.admin.browser.level

import com.arkhamusserver.arkhamus.logic.admin.AdminQuestLogic
import com.arkhamusserver.arkhamus.view.dto.admin.AdminQuestDto
import com.arkhamusserver.arkhamus.view.dto.admin.LevelTaskDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class BrowserQuestController(
    private val adminQuestLogic: AdminQuestLogic
) {

    companion object {
        val logger = LoggerFactory.getLogger(BrowserQuestController::class.java)!!
    }

    @PostMapping("/admin/browser/level/{levelId}/quest")
    fun createQuest(
        model: Model,
        redirectAttrs: RedirectAttributes,
        @PathVariable levelId: Long,
    ): String {
        val createdQuest: AdminQuestDto = adminQuestLogic.create(levelId)
        model.addAttribute("quest", createdQuest)
        addPossibleTasks(levelId, model)
        addLevelIdAttribute(model, levelId)

        redirectAttrs.addAttribute("levelId", levelId)
        redirectAttrs.addAttribute("questId", createdQuest.id)
        return "redirect:/admin/browser/level/{levelId}/quest/{questId}"
    }

    @PostMapping("/admin/browser/level/{levelId}/quest/{questId}")
    fun saveQuest(
        model: Model,
        redirectAttrs: RedirectAttributes,
        @PathVariable levelId: Long,
        @PathVariable questId: Long,
        @ModelAttribute quest: AdminQuestDto,
    ): String {
       logger.info("save by Thymeleaf")
        val createdQuest: AdminQuestDto = adminQuestLogic.save(questId, quest)
        model.addAttribute("quest", createdQuest)
        addPossibleTasks(levelId, model)
        addLevelIdAttribute(model, levelId)

        redirectAttrs.addAttribute("levelId", levelId)
        redirectAttrs.addAttribute("questId", createdQuest.id)
        return "redirect:/admin/browser/level/{levelId}/quest/{questId}"
    }

    @GetMapping("/admin/browser/level/{levelId}/quests")
    fun allQuests(
        model: Model,
        @PathVariable levelId: Long,
    ): String {
        val quests: List<AdminQuestDto> = adminQuestLogic.all(levelId)
        model.addAttribute("quests", quests)
        addLevelIdAttribute(model, levelId)
        return "quests"
    }

    @GetMapping("/admin/browser/level/{levelId}/quest/{questId}")
    fun quest(
        model: Model,
        @PathVariable levelId: Long,
        @PathVariable questId: Long,
    ): String {
        logger.info("loading quest ${questId}")
        val quest: AdminQuestDto = adminQuestLogic.get(questId)
        model.addAttribute("quest", quest)
        addLevelIdAttribute(model, levelId)
        addPossibleTasks(levelId, model)
        return "quest"
    }

    private fun addLevelIdAttribute(model: Model, levelId: Long) {
        model.addAttribute("levelId", levelId)
    }

    private fun addPossibleTasks(levelId: Long, model: Model) {
        val possibleTasks: List<LevelTaskDto> = adminQuestLogic.possibleTasks(levelId)
        model.addAttribute("possibleTasks", possibleTasks)
    }
}