package com.arkhamusserver.arkhamus.service

import com.arkhamusserver.arkhamus.getBuildType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Service for retrieving application version and build type information.
 */
@Service
class VersionService {

    @Value("\${spring.application.version:0.0.1-SNAPSHOT}")
    private lateinit var applicationVersion: String

    /**
     * Get the current build type (test or release).
     *
     * @return The current build type as a string.
     */
    fun getCurrentBuildType(): String {
        return getBuildType()
    }

    /**
     * Get the current application version.
     *
     * @return The current application version as a string.
     */
    fun getVersion(): String {
        return applicationVersion
    }

    /**
     * Get both build type and version information.
     *
     * @return A map containing build type and version information.
     */
    fun getVersionInfo(): Map<String, String> {
        return mapOf(
            "buildType" to getCurrentBuildType(),
            "version" to applicationVersion
        )
    }
}
