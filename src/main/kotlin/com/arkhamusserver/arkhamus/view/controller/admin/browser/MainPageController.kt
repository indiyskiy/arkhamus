package com.arkhamusserver.arkhamus.view.controller.admin.browser

import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class MainPageController {

    companion object {
        val logger = LoggerFactory.getLogger(MainPageController::class.java)!!
    }

    @GetMapping("/")
    fun mainPage(
        response: HttpServletResponse,
        model: Model
    ): String {
        logger.info("some one is here")
        return "main"
    }
}