package com.arkhamusserver.arkhamus

import com.arkhamusserver.arkhamus.model.dataaccess.UserAccountRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class ArkhamusApplication {

    private val log: Logger = LoggerFactory.getLogger(ArkhamusApplication::class.java)

    @Bean
    fun testDatabaseRequests(repository: UserAccountRepository): CommandLineRunner {
        return CommandLineRunner { args: Array<String?>? ->

        }
    }
}

fun main(args: Array<String>) {
    runApplication<ArkhamusApplication>(*args)
}
