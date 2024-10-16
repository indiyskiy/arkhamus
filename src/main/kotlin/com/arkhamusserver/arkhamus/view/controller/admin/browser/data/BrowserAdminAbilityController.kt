package com.arkhamusserver.arkhamus.view.controller.admin.browser.data

import com.arkhamusserver.arkhamus.logic.admin.AdminAbilityLogic
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.concurrent.TimeUnit

@Controller
class BrowserAdminAbilityController(
    private val abilityLogic: AdminAbilityLogic
) {
    @GetMapping("/admin/browser/abilities")
    fun abilities(model: Model): String {
        val abilities = abilityLogic.listAllAbilities()
        model.addAttribute("abilities", abilities)
        return "abilities"
    }

    @GetMapping("/admin/browser/abilities/{id}")
    fun ability(
        model: Model,
        @PathVariable id: Int,
    ): String {
        val ability = abilityLogic.getAbility(id)!!
        model.addAttribute("ability", ability)
        model.addAttribute("cooldownFormatted", formatMillis(ability.cooldown))
        model.addAttribute("activeFormatted", ability.active?.let { formatMillis(it) })
        return "ability"
    }

    private fun formatMillis(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        val milliseconds = millis % 1000
        return String.format("%02d min %02d sec %03d millis", minutes, seconds, milliseconds)
    }
}