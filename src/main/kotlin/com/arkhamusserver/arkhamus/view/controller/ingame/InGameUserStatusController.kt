package com.arkhamusserver.arkhamus.view.controller.ingame

import com.arkhamusserver.arkhamus.logic.ingame.InGameUserStatusLogic
import com.arkhamusserver.arkhamus.view.dto.ingame.InGameUserStatusDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("public/userStatus")
class InGameUserStatusController(
    private val inGameUserStatusLogic: InGameUserStatusLogic
) {
    @GetMapping
    fun getAllInGameStatuses(): ResponseEntity<List<InGameUserStatusDto>> {
        val abilities = inGameUserStatusLogic.listAllInUserGameStatuses()
        return ResponseEntity.ok(abilities)
    }
}