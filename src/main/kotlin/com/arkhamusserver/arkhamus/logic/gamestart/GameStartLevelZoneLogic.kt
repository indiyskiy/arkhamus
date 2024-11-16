package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLevelEllipseRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLevelTetragonRepository
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLevelZoneRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.EllipseRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelZoneRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.TetragonRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZone
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZoneEllipse
import com.arkhamusserver.arkhamus.model.redis.RedisLevelZoneTetragon
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameStartLevelZoneLogic(
    private val levelZoneRepository: LevelZoneRepository,
    private val redisLevelZoneRepository: RedisLevelZoneRepository,
    private val tetragonRepository: TetragonRepository,
    private val ellipseRepository: EllipseRepository,
    private val redisLevelTetragonRepository: RedisLevelTetragonRepository,
    private val redisLevelEllipseRepository: RedisLevelEllipseRepository,
) {

    @Transactional
    fun createLevelZones(levelId: Long, game: GameSession): List<RedisLevelZone> {
        val levelZones = levelZoneRepository.findByLevelId(levelId)
        val zones = levelZones.map {
            val zone = createRedisLevelZone(it, game)
            createRedisTetragons(it, game)
            createRedisEllipses(it, game)
            zone
        }
        return zones
    }

    private fun createRedisTetragons(zone: LevelZone, game: GameSession) {
        val tetragons = tetragonRepository.findByLevelZoneId(zone.id!!)
        tetragons.forEach {
            val redisTetragon = RedisLevelZoneTetragon(
                id = generateRandomId(),
                gameId = game.id!!,
                levelZoneId = zone.inGameId,
                inGameTetragonId = it.inGameId,

                point0X = it.point0X,
                point0Y = it.point0Y,
                point0Z = it.point0Z,

                point1X = it.point1X,
                point1Y = it.point1Y,
                point1Z = it.point1Z,

                point2X = it.point2X,
                point2Y = it.point2Y,
                point2Z = it.point2Z,

                point3X = it.point3X,
                point3Y = it.point3Y,
                point3Z = it.point3Z,
            )
            redisLevelTetragonRepository.save(redisTetragon)
        }
    }

    private fun createRedisEllipses(zone: LevelZone, game: GameSession) {
        val ellipses = ellipseRepository.findByLevelZoneId(zone.id!!)
        ellipses.forEach {
            val redisEllipse = RedisLevelZoneEllipse(
                id = generateRandomId(),
                gameId = game.id!!,
                levelZoneId = zone.inGameId,
                inGameTetragonId = it.inGameId,
                pointX = it.x,
                pointY = it.y,
                pointZ = it.z,
                height = it.height,
                width = it.width,
            )
            redisLevelEllipseRepository.save(redisEllipse)
        }
    }

    private fun createRedisLevelZone(levelZone: LevelZone, game: GameSession): RedisLevelZone {
        return RedisLevelZone(
            id = generateRandomId(),
            gameId = game.id!!,
            levelZoneId = levelZone.inGameId,
            zoneType = levelZone.zoneType
        ).apply {
            redisLevelZoneRepository.save(this)
        }
    }
}