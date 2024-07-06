package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLevelTetragonRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLevelZoneRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelZoneRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.TetragonRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZoneTetragon
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class GameStartLevelZoneLogic(
    private val levelZoneRepository: LevelZoneRepository,
    private val redisLevelZoneRepository: RedisLevelZoneRepository,
    private val tetragonRepository: TetragonRepository,
    private val redisLevelTetragonRepository: RedisLevelTetragonRepository,
) {
    fun createLevelZones(levelId: Long, game: GameSession) {
        val levelZones = levelZoneRepository.findByLevelId(levelId)
        levelZones.forEach {
            val zone = createRedisLevelZone(it, game)
            createRedisTetragons(it, game)
        }
    }

    private fun createRedisTetragons(zone: LevelZone, game: GameSession) {
        val tetragons = tetragonRepository.findByLevelZoneId(zone.id!!)
        tetragons.forEach {
            val redisTetragon = RedisLevelZoneTetragon(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = game.id!!,
                levelZoneId = zone.inGameId,
                inGameTetragonId = it.inGameId,
                point0X = it.point0.x,
                point0Y = it.point0.y,
                point1X = it.point1.x,
                point1Y = it.point1.y,
                point2X = it.point2.x,
                point2Y = it.point2.y,
                point3X = it.point3.x,
                point3Y = it.point3.y,
            )
            redisLevelTetragonRepository.save(redisTetragon)
        }
    }

    private fun createRedisLevelZone(levelZone: LevelZone, game: GameSession): RedisLevelZone {
        return RedisLevelZone(
            id = Generators.timeBasedEpochGenerator().generate().toString(),
            gameId = game.id!!,
            levelZoneId = levelZone.inGameId,
            zoneType = levelZone.zoneType
        ).apply {
            redisLevelZoneRepository.save(this)
        }
    }
}