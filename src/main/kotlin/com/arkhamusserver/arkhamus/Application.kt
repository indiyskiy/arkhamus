package com.arkhamusserver.arkhamus

import com.arkhamusserver.arkhamus.config.JwtProperties
import com.arkhamusserver.arkhamus.model.dataaccess.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder


@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class Application : SpringBootServletInitializer() {

    @Autowired
    lateinit var userAccountRepository: UserAccountRepository


    @Autowired
    lateinit var encoder: PasswordEncoder

    @Bean
    fun run(repository: UserAccountRepository): CommandLineRunner {
        addDefaultUsers()
        return CommandLineRunner { _: Array<String?>? ->

        }
    }

    private fun addDefaultUsers() {
        addUser(
            "indiyskiy@gmail.com",
            "indiyskiy",
            role = Role.ADMIN
        )
        addUser(
            "grafd@gmail.com",
            "Graf_D",
            role = Role.ADMIN
        )
        addUser(
            "sithoid@gmail.com",
            "sithoid",
            role = Role.USER
        )
        addUser(
            "qchan@gmail.com",
            "q-chan",
            role = Role.USER
        )
    }

    private fun addUser(
        email: String,
        nickName: String,
        password: String = "awesomepassword",
        role: Role
    ) {
        if (!userAccountRepository.findByEmail(email).isPresent) {
            userAccountRepository.save(
                UserAccount().apply {
                    this.nickName = nickName
                    this.email = email
                    this.password = encoder.encode(password)
                    this.role = role
                }
            )
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
