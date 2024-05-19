package com.arkhamusserver.arkhamus.view.controller.admin.browser

import com.arkhamusserver.arkhamus.logic.admin.AdminUserLogic
import com.arkhamusserver.arkhamus.view.dto.user.AdminUserDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class BrowserAdminUserController(
    private val adminUserLogic: AdminUserLogic
) {
    @GetMapping("/admin/browser/user")
    fun adminUserDashboard(model: Model): String {
        // Retrieve your list of AdminUserDto here

        val users: List<AdminUserDto> = adminUserLogic.all()

        // Add them as an attribute to the model
        model.addAttribute("users", users)

        // Return the view name (the configured view resolver will map this to a Thymeleaf template)
        // Assuming your Thymeleaf template is at /resources/templates/adminUser.html
        return "userList"
    }
}