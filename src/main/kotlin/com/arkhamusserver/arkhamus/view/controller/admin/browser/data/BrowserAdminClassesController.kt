package com.arkhamusserver.arkhamus.view.controller.admin.browser.data

import com.arkhamusserver.arkhamus.logic.ingame.ClassInGameLogic
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class BrowserAdminClassesController(
    private val classInGameLogic: ClassInGameLogic
) {
    @GetMapping("/admin/browser/classes")
    fun abilities(model: Model): String {
        val classes = classInGameLogic.listAllClasses()
        model.addAttribute("classesInGame", classes)
        return "classes"
    }

//    @GetMapping("/admin/browser/classes/{id}")
//    fun ability(
//        model: Model,
//        @PathVariable id: Int,
//    ): String {
//        val ability = abilityLogic.getAbility(id)!!
//        model.addAttribute("ability", ability)
//        model.addAttribute("cooldownFormatted", formatMillis(ability.cooldown))
//        model.addAttribute("activeFormatted", ability.active?.let { formatMillis(it) })
//        return "ability"
//    }

}