package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLevelZoneRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelZoneRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import org.springframework.stereotype.Component

@Component
class GameStartLevelZoneLogic(
    private val levelZoneRepository: LevelZoneRepository,
    private val redisLevelZoneRepository: RedisLevelZoneRepository,
) {
    fun createLevelZones(levelId: Long, game: GameSession) {
        val levelZones = levelZoneRepository.findByLevelId(levelId)
        levelZones.forEach {
            createRedisLevelZone(it, game)
        }
    }

    private fun createRedisLevelZone(levelZone: LevelZone, game: GameSession) {
        RedisLevelZone(
            id = "id",
            gameId = game.id!!,
            levelZoneId = levelZone.inGameId,
            zoneType = levelZone.zoneType
        ).apply {
            redisLevelZoneRepository.save(this)
        }
    }
}