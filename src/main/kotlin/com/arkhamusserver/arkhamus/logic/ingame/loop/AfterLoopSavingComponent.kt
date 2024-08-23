package com.arkhamusserver.arkhamus.logic.ingame.loop

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisGameUserRepository
import com.arkhamusserver.arkhamus.model.redis.RedisGame
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AfterLoopSavingComponent(
    private val gameUserRedisRepository: RedisGameUserRepository,
    private val gameRepository: RedisGameRepository,
) {

    @Transactional
    fun saveAll(globalGameData: GlobalGameData, game: RedisGame) {
        saveAllUsers(globalGameData)
        gameRepository.save(game)
    }

    private fun saveAllUsers(globalGameData: GlobalGameData) {
        globalGameData.users.forEach { gameUser ->
            gameUserRedisRepository.save(gameUser.value)
        }
    }
}