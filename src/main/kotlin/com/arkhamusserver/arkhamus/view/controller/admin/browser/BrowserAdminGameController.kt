package com.arkhamusserver.arkhamus.view.controller.admin.browser

import com.arkhamusserver.arkhamus.logic.admin.AdminGameLogic
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class BrowserAdminGameController(
    private val adminGameLogic: AdminGameLogic
) {
    @GetMapping("/admin/browser/game")
    fun adminUserDashboard(model: Model): String {
        val games: List<GameSessionDto> = adminGameLogic.all()
        model.addAttribute("games", games)
        return "gameList"
    }
}