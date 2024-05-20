package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.logic.LevelLogic
import com.arkhamusserver.arkhamus.view.dto.LevelDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("public/level")
class LevelController(
    private val levelLogic: LevelLogic
) {
    @GetMapping
    fun all(
    ): List<LevelDto> =
        levelLogic.all()
}