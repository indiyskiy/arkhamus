package com.arkhamusserver.arkhamus.utils

import com.arkhamusserver.arkhamus.model.dataaccess.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EnvironmentSetupUtil {

    @Autowired
    lateinit var userAccountRepository: UserAccountRepository

    fun setupEnvironment() {
        addSomePlayers(10)
    }

    private fun addSomePlayers(playerCount: Int) {
        repeat(playerCount) {
            UserAccount().apply {
                nickName = "Player $it"
                email = "player$it@example.com"
                userAccountRepository.save(this)
            }
        }
    }
}