package com.arkhamusserver.arkhamus.view.controller.ingame

import com.arkhamusserver.arkhamus.logic.ingame.quest.QuestReadLogic
import com.arkhamusserver.arkhamus.view.dto.ingame.QuestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("quest")
class ClientQuestController(
    private val questLogic: QuestReadLogic
) {
    @GetMapping
    fun getAllQuests(): ResponseEntity<List<QuestDto>> {
        val quests = questLogic.listAllQuests()
        return ResponseEntity.ok(quests)
    }

    @GetMapping("level/{levelId}")
    fun getAllQuests(
        @PathVariable levelId: Long
    ): ResponseEntity<List<QuestDto>> {
        val quests = questLogic.listQuests(levelId)
        return ResponseEntity.ok(quests)
    }
}