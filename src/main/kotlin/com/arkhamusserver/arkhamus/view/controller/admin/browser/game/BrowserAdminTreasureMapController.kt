package com.arkhamusserver.arkhamus.view.controller.admin.browser.game

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class BrowserAdminTreasureMapController(
) {
    @GetMapping("/public/treasureMap")
    fun level(): String {
        return "treasureMap"
    }
}