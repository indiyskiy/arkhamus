package com.arkhamusserver.arkhamus.view.controller.admin.browser

import com.arkhamusserver.arkhamus.logic.admin.AdminGameLogic
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameSessionDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BrowserAdminGameController(
    private val adminGameLogic: AdminGameLogic
) {
    @GetMapping("/admin/browser/game")
    fun adminGameDashboard(model: Model): String {
        val games: List<GameSessionDto> = adminGameLogic.all()
        model.addAttribute("games", games)
        return "gameList"
    }

    @GetMapping("/admin/browser/game/{gameId}")
    fun adminGame(
        @PathVariable gameId: Long,
        model: Model
    ): String {
        val game: AdminGameSessionDto = adminGameLogic.game(gameId)
        model.addAttribute("game", game)
        return "game"
    }
}