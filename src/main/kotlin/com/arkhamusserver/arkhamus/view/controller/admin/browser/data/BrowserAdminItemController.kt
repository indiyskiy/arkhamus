package com.arkhamusserver.arkhamus.view.controller.admin.browser.data

import com.arkhamusserver.arkhamus.logic.ingame.item.ItemLogic
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BrowserAdminItemController(
    private val itemLogic: ItemLogic
) {
    @GetMapping("/admin/browser/items")
    fun items(model: Model): String {
        val items = itemLogic.listAllItems()
        model.addAttribute("items", items)
        return "items"
    }

    @GetMapping("/admin/browser/items/{id}")
    fun item(
        model: Model,
        @PathVariable id: Int,
    ): String {
        val item = itemLogic.listAllItems().first { it.id == id }
        model.addAttribute("item", item)
        val recipes = itemLogic.listAllRecipes().filter { it.item.id == item.id }
        model.addAttribute("recipes", recipes)
        return "item"
    }
}