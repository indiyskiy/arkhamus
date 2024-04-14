package com.arkhamusserver.arkhamus.view.controller.ingame

import com.arkhamusserver.arkhamus.logic.ingame.ClassInGameLogic
import com.arkhamusserver.arkhamus.view.dto.ingame.ClassInGameDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("public/classingame")
class ClassInGameController(
    private val classInGameLogic: ClassInGameLogic
) {
    @GetMapping
    fun getAllClasses(): ResponseEntity<List<ClassInGameDto>> {
        val roles = classInGameLogic.listAllClasses()
        return ResponseEntity.ok(roles)
    }
}