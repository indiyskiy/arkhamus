package com.arkhamusserver.arkhamus.view.controller.admin.browser.level

import com.arkhamusserver.arkhamus.logic.admin.AdminLevelInfoLogic
import com.arkhamusserver.arkhamus.logic.admin.AdminLevelPreviewLogic
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameLevelGeometryDto
import com.arkhamusserver.arkhamus.view.dto.admin.AdminGameLevelInfoDto
import com.arkhamusserver.arkhamus.view.dto.admin.LevelFilterDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class BrowserAdminLevelController(
    private val adminLevelPreviewLogic: AdminLevelPreviewLogic,
    private val adminLevelInfoLogic: AdminLevelInfoLogic
) {
    @GetMapping("/admin/browser/levels")
    fun levels(
        model: Model,
    ): String {
        val levelGeometry: List<AdminGameLevelInfoDto> = adminLevelInfoLogic.all()
        model.addAttribute("levelInfo", levelGeometry)
        return "levels"
    }

    @GetMapping("/admin/browser/level/{levelId}")
    fun level(
        @PathVariable levelId: Long,
        model: Model,
    ): String {
        val levelGeometry: AdminGameLevelInfoDto = adminLevelInfoLogic.info(levelId)
        model.addAttribute("levelInfo", levelGeometry)
        return "level"
    }

    @GetMapping("/admin/browser/level/{levelId}/preview")
    fun levelPreview(
        @PathVariable levelId: Long,
        model: Model,
    ): String {
        val levelGeometry: AdminGameLevelGeometryDto = adminLevelPreviewLogic.geometry(levelId)
        model.addAttribute("levelGeometry", levelGeometry)
        return "levelPreview"
    }

    @PostMapping("/admin/browser/level/{levelId}/preview")
    fun receiveLevelDetails(
        @PathVariable levelId: Long,
        @ModelAttribute levelFilterDto: LevelFilterDto,
        model: Model
    ): String {
        val levelGeometry: AdminGameLevelGeometryDto = adminLevelPreviewLogic.geometry(levelId, levelFilterDto)
        model.addAttribute("levelGeometry", levelGeometry)
        return "levelPreview"
    }
}