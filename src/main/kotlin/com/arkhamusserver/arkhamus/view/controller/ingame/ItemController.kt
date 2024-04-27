package com.arkhamusserver.arkhamus.view.controller.ingame

import com.arkhamusserver.arkhamus.logic.ingame.item.ItemLogic
import com.arkhamusserver.arkhamus.view.dto.ingame.ItemInformationDto
import com.arkhamusserver.arkhamus.view.dto.ingame.RecipeDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("public/item")
class ItemController(
    private val itemLogic: ItemLogic
) {
    @GetMapping
    fun getAllItems(): ResponseEntity<List<ItemInformationDto>> {
        val items = itemLogic.listAllItems()
        return ResponseEntity.ok(items)
    }

    @GetMapping("recipes")
    fun getAllRecipes(): ResponseEntity<List<RecipeDto>> {
        val items = itemLogic.listAllRecipes()
        return ResponseEntity.ok(items)
    }
}