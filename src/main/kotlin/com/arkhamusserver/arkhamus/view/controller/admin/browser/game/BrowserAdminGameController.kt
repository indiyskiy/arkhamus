package com.arkhamusserver.arkhamus.view.controller.admin.browser.game

import com.arkhamusserver.arkhamus.logic.admin.AdminGameLogic
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameSessionDto
import com.arkhamusserver.arkhamus.view.dto.admin.GameStatisticHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

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

    @GetMapping("/admin/browser/game/{gameId}/preview")
    fun adminGamePreview(
        @PathVariable gameId: Long,
        model: Model
    ): String {
        // Add required attributes to the model, including the list of users and activity types
        val users = adminGameLogic.getAllUsers(gameId)
        val types = ActivityType.values().toList()
        model.addAttribute("users", users)
        model.addAttribute("activityTypes", types)
        model.addAttribute("gameActivities", adminGameLogic.getGameActivities(gameId, users.map { it.id }, types))
        return "gamePreview"
    }

    @PostMapping("/admin/browser/game/{gameId}/preview")
    fun handlePreview(
        @PathVariable gameId: Long,
        @RequestParam userIds: List<Long>,
        @RequestParam activityTypes: List<ActivityType>,
        model: Model
    ): String {
        val gameActivities = adminGameLogic.getGameActivities(
            gameId = gameId,
            userIds = userIds,
            activityTypes = activityTypes
        )
        model.addAttribute("gameActivities", gameActivities)
        model.addAttribute("users", adminGameLogic.getAllUsers(gameId))
        model.addAttribute("activityTypes", ActivityType.values().toList())
        return "gamePreview"
    }
}