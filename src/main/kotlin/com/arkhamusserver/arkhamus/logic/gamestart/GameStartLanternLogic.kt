package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLanternRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.utils.GameRelatedIdSource
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.LanternRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.Lantern
import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GameStartLanternLogic(
    private val redisLanternRepository: RedisLanternRepository,
    private val lanternRepository: LanternRepository,
    private val gameRelatedIdSource: GameRelatedIdSource,
) {
    private val random: Random = Random(System.currentTimeMillis())

    fun createLanterns(
        levelId: Long,
        game: GameSession
    ) {
        val allLevelLanterns = lanternRepository.findByLevelId(levelId)
        allLevelLanterns.shuffled(random).forEachIndexed { i, dbLantern ->
            with(createLantern(game, dbLantern, (i % 2 == 0))) {
                redisLanternRepository.save(this)
            }
        }
    }

    private fun createLantern(
        game: GameSession,
        dbLantern: Lantern,
        filled: Boolean
    ) = RedisLantern(
        id = gameRelatedIdSource.getId(game.id!!, dbLantern.inGameId!!),
        lanternId = dbLantern.inGameId!!,
        gameId = game.id!!,
        x = dbLantern.x!!,
        y = dbLantern.y!!,
        lightRange = dbLantern.lightRange!!,
        filled = filled,
        activated = false
    )

}