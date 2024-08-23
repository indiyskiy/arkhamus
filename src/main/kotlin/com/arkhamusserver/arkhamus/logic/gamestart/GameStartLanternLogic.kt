package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLanternRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LanternRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.Lantern
import com.arkhamusserver.arkhamus.model.redis.RedisLantern
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class GameStartLanternLogic(
    private val redisLanternRepository: RedisLanternRepository,
    private val lanternRepository: LanternRepository,
) {
    private val random: Random = Random(System.currentTimeMillis())

    @Transactional
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
        id = Generators.timeBasedEpochGenerator().generate().toString(),
        lanternId = dbLantern.inGameId,
        gameId = game.id!!,
        x = dbLantern.point.x,
        y = dbLantern.point.y,
        lightRange = dbLantern.lightRange!!,
        filled = filled,
        activated = false
    )

}