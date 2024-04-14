package com.arkhamusserver.arkhamus.view.controller.ingame

import com.arkhamusserver.arkhamus.logic.ingame.AbilityLogic
import com.arkhamusserver.arkhamus.view.dto.ingame.AbilityDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("public/ability")
class AbilityController(
    private val abilityLogic: AbilityLogic
) {
    @GetMapping
    fun getAllItems(): ResponseEntity<List<AbilityDto>> {
        val abilities = abilityLogic.listAllAbilities()
        return ResponseEntity.ok(abilities)
    }
}