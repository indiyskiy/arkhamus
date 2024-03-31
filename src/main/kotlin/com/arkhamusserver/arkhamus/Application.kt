package com.arkhamusserver.arkhamus

import com.arkhamusserver.arkhamus.config.auth.JwtProperties
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
@EnableScheduling
class Application : SpringBootServletInitializer() {

    @Bean
    fun run(repository: UserAccountRepository): CommandLineRunner {
        return CommandLineRunner { _: Array<String?>? ->
        }
    }

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
