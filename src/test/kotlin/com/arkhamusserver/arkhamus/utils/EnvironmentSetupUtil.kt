package com.arkhamusserver.arkhamus.utils

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.Role
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.RoleName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EnvironmentSetupUtil {

    companion object {
        const val PLAYERS_NUMBER_TO_ADD = 10
    }

    @Autowired
    lateinit var userAccountRepository: UserAccountRepository

    fun setupEnvironment() {
        addSomePlayers()
    }

    private fun addSomePlayers(playerCount: Int = PLAYERS_NUMBER_TO_ADD) {
        repeat(playerCount) {
            UserAccount(nickName = "Player $it").apply {
                email = "player$it@example.com"
                password = "${it}omgCoolPassword${it}"
                role = setOf(
                    Role(name = RoleName.USER.securityValue).apply {
                        id = 1
                    }
                )
                userAccountRepository.save(this)
            }
        }
    }
}