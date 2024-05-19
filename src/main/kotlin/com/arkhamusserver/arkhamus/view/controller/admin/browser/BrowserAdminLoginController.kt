package com.arkhamusserver.arkhamus.view.controller.admin.browser

import com.arkhamusserver.arkhamus.view.dto.user.AuthenticationRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class BrowserAdminLoginController() {
    @GetMapping("/public/browser/login")
    fun login(model: Model): String {
        model.addAttribute("login", AuthenticationRequest("", ""))
        return "login"
    }
}