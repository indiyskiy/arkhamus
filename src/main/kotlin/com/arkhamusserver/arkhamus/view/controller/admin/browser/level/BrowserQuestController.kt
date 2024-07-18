package com.arkhamusserver.arkhamus.view.controller.admin.browser.level

import com.arkhamusserver.arkhamus.logic.admin.AdminQuestLogic
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState
import com.arkhamusserver.arkhamus.view.dto.admin.AdminQuestDto
import com.arkhamusserver.arkhamus.view.dto.admin.AdminLevelTaskDto
import com.arkhamusserver.arkhamus.view.dto.admin.AdminQuestGiverDto
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
        addDefaultPageValues(levelId, model)
        return setUpRedirect(redirectAttrs, levelId, createdQuest.id)
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
        val editedQuest: AdminQuestDto = adminQuestLogic.save(questId, quest)
        model.addAttribute("quest", editedQuest)
        addDefaultPageValues(levelId, model)
        return setUpRedirect(redirectAttrs, levelId, editedQuest.id)
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
        logger.info("loading quest $questId")
        val quest: AdminQuestDto = adminQuestLogic.get(questId)
        model.addAttribute("quest", quest)
        addDefaultPageValues(levelId, model)
        return "quest"
    }

    private fun addDefaultPageValues(levelId: Long, model: Model) {
        addPossibleStates(model)
        addPossibleTasks(levelId, model)
        addPossibleQuestGivers(levelId, model)
        addLevelIdAttribute(model, levelId)
    }

    private fun setUpRedirect(
        redirectAttrs: RedirectAttributes,
        levelId: Long,
        questId: Long
    ): String {
        redirectAttrs.addAttribute("levelId", levelId)
        redirectAttrs.addAttribute("questId", questId)
        return "redirect:/admin/browser/level/{levelId}/quest/{questId}"
    }

    private fun addLevelIdAttribute(model: Model, levelId: Long) {
        model.addAttribute("levelId", levelId)
    }

    private fun addPossibleTasks(levelId: Long, model: Model) {
        val possibleTasks: List<AdminLevelTaskDto> = adminQuestLogic.possibleTasks(levelId)
        model.addAttribute("possibleTasks", possibleTasks)
    }

    private fun addPossibleQuestGivers(levelId: Long, model: Model) {
        val possibleQuestGivers: List<AdminQuestGiverDto> = adminQuestLogic.possibleQuestGivers(levelId)
        model.addAttribute("possibleQuestGivers", possibleQuestGivers)
    }

    private fun addPossibleStates(model: Model) {
        val possibleStates: List<String> = QuestState.values().map { it.name }
        model.addAttribute("possibleStates", possibleStates)
    }
}