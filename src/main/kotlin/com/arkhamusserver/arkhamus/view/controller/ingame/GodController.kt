package com.arkhamusserver.arkhamus.view.controller.ingame

import com.arkhamusserver.arkhamus.view.dto.ingame.GodDto
import com.arkhamusserver.arkhamus.view.dto.ingame.GodWithCorksDto
import com.arkhamusserver.arkhamus.logic.ingame.GodLogic
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("public/god")
class GodController(
    private val godLogic: GodLogic
) {
    @GetMapping
    fun getAllItems(): ResponseEntity<List<GodDto>> {
        val gods = godLogic.listAllGods()
        return ResponseEntity.ok(gods)
    }
    @GetMapping("corks")
    fun getGodsWithCorks(): ResponseEntity<List<GodWithCorksDto>> {
        val gods = godLogic.getGodsWithCorks()
        return ResponseEntity.ok(gods)
    }
}