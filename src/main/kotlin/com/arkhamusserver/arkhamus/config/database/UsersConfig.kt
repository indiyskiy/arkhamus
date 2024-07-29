package com.arkhamusserver.arkhamus.config.database

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.RoleRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserSkinRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.database.entity.UserSkinSettings
import com.arkhamusserver.arkhamus.model.database.entity.game.Role
import com.arkhamusserver.arkhamus.model.enums.RoleName
import com.arkhamusserver.arkhamus.model.enums.SkinColor
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class UsersConfig {
    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userAccountRepository: UserAccountRepository

    @Autowired
    lateinit var userSkinRepository: UserSkinRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    companion object {
        var logger: Logger = LoggerFactory.getLogger(UsersConfig::class.java)
        private val random = Random(System.currentTimeMillis())
    }

    @PostConstruct
    fun addDefaultUsers() {
        val roles = createDefaultRoles()
        addDefaultUsers(roles)
    }

    private fun createDefaultRoles(): Map<RoleName, Role> =
        RoleName.values().associateWith { getOrCreate(it) }

    private fun getOrCreate(roleName: RoleName): Role {
        val old = roleRepository.findByName(roleName.securityValue).orElse(null)
        if (old != null) {
            return old
        } else {
            Role(
                name = roleName.securityValue
            ).also { role ->
                return roleRepository.save(role)
            }
        }
    }


    fun addDefaultUsers(roles: Map<RoleName, Role>) {
        logger.info("add default users")
        addUser(
            "indiyskiy@gmail.com",
            "indiyskiy",
            role = roles[RoleName.ADMIN]!!
        )
        addUser(
            "grafd@gmail.com",
            "Graf_D",
            role = roles[RoleName.ADMIN]!!
        )
        addUser(
            "sithoid@gmail.com",
            "sithoid",
            role = roles[RoleName.ADMIN]!!
        )
        addUser(
            "mars@gmail.com",
            "mars",
            role = roles[RoleName.ADMIN]!!
        )
        addUser(
            "evans@gmail.com",
            "evans",
            role = roles[RoleName.ADMIN]!!
        )
        repeat(100) { i ->
            addUser(
                "test${i}@gmail.com",
                "test $i",
                password = "LetMe1n!!!",
                role = roles[RoleName.USER]!!
            )
        }
    }

    private fun addUser(
        email: String,
        nickName: String,
        password: String = "awesomepassword",
        role: Role
    ) {
        if (!userAccountRepository.findByEmail(email).isPresent) {
            val userAccount = userAccountRepository.save(
                UserAccount(
                    nickName = nickName,
                    email = email,
                    password = encoder.encode(password),
                    role = setOf(role),
                )
            )
            userSkinRepository.save(
                generateSkin(userAccount)
            )
        }
    }

    private fun generateSkin(userAccount: UserAccount): UserSkinSettings {
        return UserSkinSettings(
            skinColor = randomSkinColor(),
            userAccount = userAccount
        )
    }

    private fun randomSkinColor(): SkinColor {
        return SkinColor.values().random(random)
    }
}