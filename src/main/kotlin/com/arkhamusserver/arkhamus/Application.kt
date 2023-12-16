package com.arkhamusserver.arkhamus

import com.arkhamusserver.arkhamus.config.JwtProperties
import com.arkhamusserver.arkhamus.model.dataaccess.UserAccountRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean


@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class Application : SpringBootServletInitializer() {

//    private val log: Logger = LoggerFactory.getLogger(Application::class.java)
//
//    @Autowired
//    lateinit var userAccountRepository: UserAccountRepository
//
//
//    @Autowired
//    lateinit var encoder: PasswordEncoder

    @Bean
    fun testDatabaseRequests(repository: UserAccountRepository): CommandLineRunner {
//       if(!userAccountRepository.findByEmail("indiyskiy@gmail.com").isPresent){
//           userAccountRepository.save(
//               UserAccount().apply {
//                  nickName = "indiyskiy"
//                  email = "indiyskiy@gmail.com"
//                  password =  encoder.encode("awesomepassword")
//                  role = Role.ADMIN
//               }
//           )
//       }
        return CommandLineRunner { args: Array<String?>? ->

        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
