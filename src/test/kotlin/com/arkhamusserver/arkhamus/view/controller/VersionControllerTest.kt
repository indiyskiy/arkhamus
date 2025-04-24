package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.service.VersionService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class VersionControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var versionService: VersionService
    private lateinit var versionController: VersionController

    @BeforeEach
    fun setup() {
        versionService = org.mockito.Mockito.mock(VersionService::class.java)
        versionController = VersionController(versionService)

        // Configure a ViewResolver to handle the "version" view name
        val viewResolver = org.springframework.web.servlet.view.InternalResourceViewResolver()
        viewResolver.setPrefix("/WEB-INF/templates/")
        viewResolver.setSuffix(".html")

        mockMvc = MockMvcBuilders.standaloneSetup(versionController)
            .setViewResolvers(viewResolver)
            .build()
    }

    @Test
    fun `should return version page with build type and version`() {
        // Given
        val buildType = "test"
        val version = "0.0.1-SNAPSHOT"
        `when`(versionService.getCurrentBuildType()).thenReturn(buildType)
        `when`(versionService.getVersion()).thenReturn(version)

        // When/Then
        mockMvc.perform(get("/public/version"))
            .andExpect(status().isOk)
            .andExpect(view().name("version"))
            .andExpect(model().attribute("buildType", buildType))
            .andExpect(model().attribute("version", version))

        verify(versionService).getCurrentBuildType()
        verify(versionService).getVersion()
    }

    @Test
    fun `should return version info as JSON`() {
        // Given
        val versionInfo = mapOf(
            "buildType" to "test",
            "version" to "0.0.1-SNAPSHOT"
        )
        `when`(versionService.getVersionInfo()).thenReturn(versionInfo)

        // When/Then
        mockMvc.perform(get("/public/version/json"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.buildType").value("test"))
            .andExpect(jsonPath("$.version").value("0.0.1-SNAPSHOT"))

        verify(versionService).getVersionInfo()
    }
}
