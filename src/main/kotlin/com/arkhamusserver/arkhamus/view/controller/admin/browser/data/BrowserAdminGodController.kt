package com.arkhamusserver.arkhamus.view.controller.admin.browser.data

import com.arkhamusserver.arkhamus.logic.ingame.GodLogic
import com.arkhamusserver.arkhamus.logic.ingame.item.GodToCorkResolver
import com.arkhamusserver.arkhamus.logic.ingame.item.ItemLogic
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BrowserAdminGodController(
    private val godLogic: GodLogic,
    private val itemLogic: ItemLogic,
    private val corkResolver: GodToCorkResolver
) {
    @GetMapping("/admin/browser/gods")
    fun gods(model: Model): String {
        val gods = godLogic.listAllGods()
        model.addAttribute("gods", gods)
        return "gods"
    }

    @GetMapping("/admin/browser/gods/{id}")
    fun god(
        model: Model,
        @PathVariable id: Int,
    ): String {
        val god = godLogic.listAllGods().first { it.id == id }
        model.addAttribute("god", god)
        val cork = corkResolver.resolve(id)
        val recipe = itemLogic.listAllRecipes().first { it.item.id == cork.id }
        model.addAttribute("recipe", recipe)
        return "god"
    }
}