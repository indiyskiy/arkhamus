package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameGameUserRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InRamGameRepository
import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AfterLoopSavingComponent(
    private val inGameGameUserRepository: InGameGameUserRepository,
    private val gameRepository: InRamGameRepository,
) {

    @Transactional
    fun saveAll(globalGameData: GlobalGameData, game: InRamGame) {
        saveAllUsers(globalGameData)
        gameRepository.save(game)
    }

    private fun saveAllUsers(globalGameData: GlobalGameData) {
        globalGameData.users.forEach { gameUser ->
            inGameGameUserRepository.save(gameUser.value)
        }
    }
}