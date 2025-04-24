package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.service.VersionService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Controller for providing application version information.
 */
@Controller
@RequestMapping("/public/version")
class VersionController(
    private val versionService: VersionService
) {

    /**
     * Display the version information page.
     *
     * @param model The Spring MVC model.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping
    fun getVersionPage(model: Model): String {
        model.addAttribute("buildType", versionService.getCurrentBuildType())
        model.addAttribute("version", versionService.getVersion())
        return "version"
    }

    /**
     * Provide version information as JSON.
     *
     * @return A map containing build type and version information.
     */
    @GetMapping("/json")
    @ResponseBody
    fun getVersionJson(): Map<String, String> {
        return versionService.getVersionInfo()
    }
}