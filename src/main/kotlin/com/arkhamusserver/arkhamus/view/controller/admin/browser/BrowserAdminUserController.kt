package com.arkhamusserver.arkhamus.view.controller.admin.browser

import com.arkhamusserver.arkhamus.logic.admin.AdminGameLogic
import com.arkhamusserver.arkhamus.logic.admin.AdminUserLogic
import com.arkhamusserver.arkhamus.view.dto.admin.AdminUserGameSessionDto
import com.arkhamusserver.arkhamus.view.dto.user.AdminUserDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BrowserAdminUserController(
    private val adminUserLogic: AdminUserLogic,
    private val adminGameLogic: AdminGameLogic
) {
    @GetMapping("/admin/browser/user")
    fun userDashboard(model: Model): String {
        val users: List<AdminUserDto> = adminUserLogic.all()
        model.addAttribute("users", users)
        return "userList"
    }

    @GetMapping("/admin/browser/user/{userId}")
    fun user(
        @PathVariable userId: Long,
        model: Model
    ): String {
        val user: AdminUserDto = adminUserLogic.user(userId)
        val games: List<AdminUserGameSessionDto> = adminGameLogic.allForUser(userId)
        model.addAttribute("user", user)
        model.addAttribute("games", games)
        return "user"
    }
}