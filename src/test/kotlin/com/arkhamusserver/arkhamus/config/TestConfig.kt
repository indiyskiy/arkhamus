package com.arkhamusserver.arkhamus.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * Test configuration for unit tests.
 * Provides test-specific beans and configurations.
 */
@TestConfiguration
class TestConfig {
    
    /**
     * Provides a test-specific build type.
     * This is used by the VersionService to determine the current build type.
     */
    @Bean
    @Primary
    fun buildType(): String {
        return "test"
    }
}