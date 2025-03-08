package com.arkhamusserver.arkhamus.logic.gamestart

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameLevelEllipseRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameLevelTetragonRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameLevelZoneRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.EllipseRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.LevelZoneRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.TetragonRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelZone
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZone
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZoneEllipse
import com.arkhamusserver.arkhamus.model.ingame.InGameLevelZoneTetragon
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GameStartLevelZoneLogic(
    private val levelZoneRepository: LevelZoneRepository,
    private val inGameLevelZoneRepository: InGameLevelZoneRepository,
    private val tetragonRepository: TetragonRepository,
    private val ellipseRepository: EllipseRepository,
    private val inGameLevelTetragonRepository: InGameLevelTetragonRepository,
    private val inGameLevelEllipseRepository: InGameLevelEllipseRepository,
) {

    @Transactional
    fun createLevelZones(levelId: Long, game: GameSession): List<InGameLevelZone> {
        val levelZones = levelZoneRepository.findByLevelId(levelId)
        val zones = levelZones.map {
            val zone = createInGameLevelZone(it, game)
            createInGameTetragons(it, game)
            createInGameEllipses(it, game)
            zone
        }
        return zones
    }

    private fun createInGameTetragons(zone: LevelZone, game: GameSession) {
        val tetragons = tetragonRepository.findByLevelZoneId(zone.id!!)
        tetragons.forEach {
            val inGameTetragon = InGameLevelZoneTetragon(
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
            inGameLevelTetragonRepository.save(inGameTetragon)
        }
    }

    private fun createInGameEllipses(zone: LevelZone, game: GameSession) {
        val ellipses = ellipseRepository.findByLevelZoneId(zone.id!!)
        ellipses.forEach {
            val inGameEllipse = InGameLevelZoneEllipse(
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
            inGameLevelEllipseRepository.save(inGameEllipse)
        }
    }

    private fun createInGameLevelZone(levelZone: LevelZone, game: GameSession): InGameLevelZone {
        return InGameLevelZone(
            id = generateRandomId(),
            gameId = game.id!!,
            levelZoneId = levelZone.inGameId,
            zoneType = levelZone.zoneType
        ).apply {
            inGameLevelZoneRepository.save(this)
        }
    }
}