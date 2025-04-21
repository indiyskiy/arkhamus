package com.arkhamusserver.arkhamus.view.controller.admin.browser

import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class MainPageController {

    @GetMapping("/")
    fun mainPage(
        response: HttpServletResponse,
        model: Model
    ): String {
        return "main"
    }

    @GetMapping("/public/browser/menu")
    fun menu(
        response: HttpServletResponse,
        model: Model
    ): String {
        return "menu"
    }
}