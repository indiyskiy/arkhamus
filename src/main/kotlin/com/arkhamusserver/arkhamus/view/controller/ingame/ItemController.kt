package com.arkhamusserver.arkhamus.view.controller.ingame

import com.arkhamusserver.arkhamus.logic.dto.ingame.ItemInformationDto
import com.arkhamusserver.arkhamus.logic.ingame.item.ItemLogic
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("public/item")
class ItemController(
    private val itemLogic: ItemLogic
) {
//    @GetMapping("game/{gameId}")
//    fun getItemsForGame(@PathVariable gameId: Long): ResponseEntity<List<ItemInformationDto>> {
//        val items = itemLogic.listAllItemsForGame(gameId)
//        return ResponseEntity.ok(items)
//    }
@GetMapping
    fun getAllItems(): ResponseEntity<List<ItemInformationDto>> {
        val items = itemLogic.listAllItems()
        return ResponseEntity.ok(items)
    }
}