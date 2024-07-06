package com.arkhamusserver.arkhamus.view.controller.admin.browser.game

import com.arkhamusserver.arkhamus.logic.admin.AdminRedisResourcesLogic
import com.arkhamusserver.arkhamus.view.dto.admin.AdminRedisResourcesInfoDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class BrowserAdminRedisResourcesController(
    private val redisResourcesLogic: AdminRedisResourcesLogic
) {
    @GetMapping("/admin/browser/redis/statistic")
    fun level(
        model: Model,
    ): String {
        val redisInfo: AdminRedisResourcesInfoDto = redisResourcesLogic.info()
        model.addAttribute("redisInfo", redisInfo)
        return "redisAdminStatistic"
    }
}