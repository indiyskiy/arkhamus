package com.arkhamusserver.arkhamus

import com.arkhamusserver.arkhamus.config.auth.JwtProperties
import com.arkhamusserver.arkhamus.config.steam.SteamProperties
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class, SteamProperties::class)
@EnableScheduling
class Application : SpringBootServletInitializer() {

    @Bean
    fun run(repository: UserAccountRepository): CommandLineRunner {
        return CommandLineRunner { _: Array<String?>? ->
        }
    }

}

fun main(args: Array<String>) {
    // Get build type from system property, environment variable, or manifest, default to "test"
    val buildType = getBuildType()

    // Run the application with the appropriate profile
    runApplication<Application>(*args) {
        setAdditionalProfiles(buildType)
    }
}

/**
 * Determines the build type from various sources.
 * Priority order:
 * 1. System property "buildType"
 * 2. Environment variable "BUILD_TYPE"
 * 3. Manifest attribute "Build-Type"
 * 4. Default to "test"
 */
fun getBuildType(): String {
    // Check system property
    System.getProperty("buildType")?.let { return it.toLowerCase() }

    // Check environment variable
    System.getenv("BUILD_TYPE")?.let { return it.toLowerCase() }

    // Check manifest
    try {
        val resources = Thread.currentThread().contextClassLoader.getResources("META-INF/MANIFEST.MF")
        while (resources.hasMoreElements()) {
            val manifest = java.util.jar.Manifest(resources.nextElement().openStream())
            manifest.mainAttributes.getValue("Build-Type")?.let { return it.toLowerCase() }
        }
    } catch (e: Exception) {
        // Ignore exceptions when reading manifest
    }

    // Default to test
    return "test"
}
