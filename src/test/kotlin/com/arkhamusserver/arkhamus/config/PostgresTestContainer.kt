package com.arkhamusserver.arkhamus.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

/**
 * Test configuration for PostgreSQL container.
 * This class sets up a PostgreSQL container for tests, replacing the H2 in-memory database.
 */
@TestConfiguration
class PostgresTestContainer {

    companion object {
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))
            .withDatabaseName("arkhamus-test-DB")
            .withUsername("in-code-admin")
            .withPassword("Egzxrf123")
            .withReuse(true)

        init {
            // Start the container
            postgres.start()

            // Set system properties for Spring to use
            System.setProperty("spring.datasource.url", postgres.jdbcUrl)
            System.setProperty("spring.datasource.username", postgres.username)
            System.setProperty("spring.datasource.password", postgres.password)

            // Register shutdown hook to stop container when JVM exits
            Runtime.getRuntime().addShutdownHook(Thread {
                if (postgres.isRunning) {
                    postgres.stop()
                }
            })
        }
    }

    /**
     * Creates and configures a PostgreSQL container for tests.
     * The container will be started before tests and stopped after tests.
     */
    @Bean
    fun postgresContainer(): PostgreSQLContainer<*> {
        return postgres
    }
}
