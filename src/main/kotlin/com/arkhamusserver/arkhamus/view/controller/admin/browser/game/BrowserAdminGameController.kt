package com.arkhamusserver.arkhamus.view.controller.admin.browser.game

import com.arkhamusserver.arkhamus.logic.admin.AdminGameLogic
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameSessionDto
import com.arkhamusserver.arkhamus.view.dto.admin.GameStatisticHolder
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
        val games: List<AdminGameSessionDto> = adminGameLogic.all()
        model.addAttribute("games", games)
        return "gameList"
    }

    @GetMapping("/admin/browser/gamesStatistic")
    fun adminGamesStatistic(model: Model): String {
        val gameStatisticHolder: GameStatisticHolder = adminGameLogic.statisticWinRate()
        model.addAttribute("gameStatisticHolder", gameStatisticHolder)
        return "gamesStatistic"
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