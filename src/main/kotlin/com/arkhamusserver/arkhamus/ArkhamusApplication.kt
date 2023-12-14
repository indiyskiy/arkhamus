package com.arkhamusserver.arkhamus

import com.arkhamusserver.arkhamus.model.dataaccess.UserAccountRepository
import com.arkhamusserver.arkhamus.model.dataaccess.UserOfGameSessionRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.Role
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.password.PasswordEncoder


@SpringBootApplication
class ArkhamusApplication {

    private val log: Logger = LoggerFactory.getLogger(ArkhamusApplication::class.java)

    @Autowired
    lateinit var userAccountRepository: UserAccountRepository


    @Autowired
    lateinit var encoder: PasswordEncoder

    @Bean
    fun testDatabaseRequests(repository: UserAccountRepository): CommandLineRunner {
       if(!userAccountRepository.findByEmail("indiyskiy@gmail.com").isPresent){
           userAccountRepository.save(
               UserAccount().apply {
                  nickName = "indiyskiy"
                  email = "indiyskiy@gmail.com"
                  password =  encoder.encode("awesomepassword")
                  role = Role.ADMIN
               }
           )
       }
        return CommandLineRunner { args: Array<String?>? ->

        }
    }
}

fun main(args: Array<String>) {
    runApplication<ArkhamusApplication>(*args)
}
