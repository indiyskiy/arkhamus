package com.arkhamusserver.arkhamus.view.controller.admin.browser

import com.arkhamusserver.arkhamus.model.dataaccess.UserStatusService
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class MainPageController {

    companion object {
        private val logger = LoggingUtils.getLogger<MainPageController>()
    }

    @GetMapping("/")
    fun mainPage(
        response: HttpServletResponse,
        model: Model
    ): String {
        logger.info("some one is here")
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