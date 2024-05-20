package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisAltarRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.AltarRepository
import com.arkhamusserver.arkhamus.model.database.entity.Altar
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class GameStartAltarLogic(
    private val redisAltarRepository: RedisAltarRepository,
    private val altarRepository: AltarRepository,
) {

    fun createAltars(
        levelId: Long,
        game: GameSession
    ) {
        val allLevelAltars = altarRepository.findByLevelId(levelId)
        allLevelAltars.forEach { dbAltar ->
            redisAltarRepository.save(createAltar(game, dbAltar))
        }
    }

    private fun createAltar(
        game: GameSession,
        dbAltar: Altar,
    ) = RedisAltar(
        id = Generators.timeBasedEpochGenerator().generate().toString(),
        altarId = dbAltar.inGameId!!,
        gameId = game.id!!,
        x = dbAltar.x!!,
        y = dbAltar.y!!,
        interactionRadius = dbAltar.interactionRadius!!,
    )

}