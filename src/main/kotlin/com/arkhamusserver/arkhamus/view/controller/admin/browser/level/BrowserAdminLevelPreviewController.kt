package com.arkhamusserver.arkhamus.view.controller.admin.browser.level

import com.arkhamusserver.arkhamus.logic.admin.AdminLevelPreviewLogic
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameLevelGeometryDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BrowserAdminLevelPreviewController(
    private val adminLevelPreviewLogic: AdminLevelPreviewLogic
) {
    @GetMapping("/admin/browser/level/preview/{levelId}")
    fun adminGameDashboard(
        @PathVariable levelId: Long,
        model: Model,
        ): String {
        val levelGeometry: AdminGameLevelGeometryDto = adminLevelPreviewLogic.geometry(levelId)
        model.addAttribute("levelGeometry", levelGeometry)
        return "levelPreview"
    }
}