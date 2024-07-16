package com.arkhamusserver.arkhamus.view.controller.admin.browser.level

import com.arkhamusserver.arkhamus.logic.admin.AdminQuestLogic
import com.arkhamusserver.arkhamus.view.dto.admin.AdminQuestDto
import com.arkhamusserver.arkhamus.view.dto.admin.LevelTaskDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class BrowserQuestController(
    private val adminQuestLogic: AdminQuestLogic
) {
    @PostMapping("/admin/browser/level/{levelId}/quest")
    fun createQuest(
        model: Model,
        @PathVariable levelId: Long,
    ): String {
        val createdQuest: AdminQuestDto = adminQuestLogic.create(levelId)
        model.addAttribute("quest", createdQuest)
        addPossibleTasks(levelId, model)
        addLevelIdAttribute(model, levelId)
        return "quest"
    }

    @PostMapping("/admin/browser/level/{levelId}/quest/{questId}")
    fun saveQuest(
        model: Model,
        @PathVariable levelId: Long,
        @PathVariable questId: Long,
        @ModelAttribute quest: AdminQuestDto,
    ): String {
        val createdQuest: AdminQuestDto = adminQuestLogic.save(questId, quest)
        model.addAttribute("quest", createdQuest)
        addPossibleTasks(levelId, model)
        addLevelIdAttribute(model, levelId)
        return "quest"
    }

    @PostMapping("/admin/browser/level/{levelId}/quest/{questId}/addStep")
    fun addStep(
        model: Model,
        @PathVariable levelId: Long,
        @PathVariable questId: Long,
    ): String {
        val createdQuest: AdminQuestDto = adminQuestLogic.addStep(questId)
        model.addAttribute("quest", createdQuest)
        addPossibleTasks(levelId, model)
        addLevelIdAttribute(model, levelId)
        return "quest"
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