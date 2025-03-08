package com.arkhamusserver.arkhamus.view.controller.admin.browser.tech.stat

import com.arkhamusserver.arkhamus.logic.admin.AdminNettyResourcesLogic
import com.arkhamusserver.arkhamus.view.dto.admin.AdminNettyResourcesInfoDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class BrowserAdminNettyConnectionController(
    private val nettyResourcesLogic: AdminNettyResourcesLogic
) {
    @GetMapping("/admin/browser/netty/statistic")
    fun level(
        model: Model,
    ): String {
        val nettyInfo: AdminNettyResourcesInfoDto = nettyResourcesLogic.info()
        model.addAttribute("nettyInfo", nettyInfo)
        return "nettyAdminStatistic"
    }
}