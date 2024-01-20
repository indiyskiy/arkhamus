package com.arkhamusserver.arkhamus.config.database

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.Role
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UsersConfig {
    @Autowired
    lateinit var userAccountRepository: UserAccountRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    companion object {
        var logger: Logger = LoggerFactory.getLogger(UsersConfig::class.java)
    }

    @PostConstruct
    fun addDefaultUsers() {
        logger.info("add default users")
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