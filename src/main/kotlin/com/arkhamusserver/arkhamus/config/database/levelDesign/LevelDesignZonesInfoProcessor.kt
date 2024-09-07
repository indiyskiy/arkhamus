package com.arkhamusserver.arkhamus.config.database.levelDesign

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.EllipseRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.LevelZoneRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.TetragonRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.Ellipse
import com.arkhamusserver.arkhamus.model.database.entity.game.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.LevelZone
import com.arkhamusserver.arkhamus.model.database.entity.game.Tetragon
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.view.levelDesign.EllipseFromJson
import com.arkhamusserver.arkhamus.view.levelDesign.TetragonFromJson
import com.arkhamusserver.arkhamus.view.levelDesign.ZoneFromJson
import com.arkhamusserver.arkhamus.view.validator.utils.assertTrue
import org.springframework.stereotype.Component
import kotlin.collections.forEach

@Component
class LevelDesignZonesInfoProcessor(
    private val levelZoneRepository: LevelZoneRepository,
    private val tetragonRepository: TetragonRepository,
    private val ellipseRepository: EllipseRepository,
) {
    fun processZones(clueZones: List<ZoneFromJson>, banZones: List<ZoneFromJson>, level: Level) {
        processClueZones(clueZones, level)
        processBanZones(banZones, level)
    }

    private fun processClueZones(clueZones: List<ZoneFromJson>, savedLevel: Level) {
        clueZones.forEach { clueZone ->
            val levelZone = LevelZone(
                inGameId = clueZone.zoneId!!,
                zoneType = ZoneType.CLUE,
                level = savedLevel,
            ).apply {
                levelZoneRepository.save(this)
            }
            processClueTetragons(clueZone.tetragons, levelZone)
            processClueEllipses(clueZone.ellipses, levelZone)
        }
    }

    private fun processBanZones(clueZones: List<ZoneFromJson>, savedLevel: Level) {
        clueZones.forEach { clueZone ->
            val levelZone = LevelZone(
                inGameId = clueZone.zoneId!!,
                zoneType = ZoneType.BAN,
                level = savedLevel,
            ).apply {
                levelZoneRepository.save(this)
            }
            processClueTetragons(clueZone.tetragons, levelZone)
            processClueEllipses(clueZone.ellipses, levelZone)
        }
    }

    private fun processClueTetragons(
        tetragons: List<TetragonFromJson>,
        levelZone: LevelZone,
    ) {
        tetragons.forEach { tetragon ->
            assertTrue(
                tetragon.points.size == 4,
                "Size of tetragon ${tetragon.id} is not 4",
                TetragonFromJson::class.simpleName!!
            )
            Tetragon(
                inGameId = tetragon.id!!,
                levelZone = levelZone,
                point0X = tetragon.points[0].x!!,
                point0Y = tetragon.points[0].y!!,
                point0Z = tetragon.points[0].z!!,

                point1X = tetragon.points[1].x!!,
                point1Y = tetragon.points[1].y!!,
                point1Z = tetragon.points[1].z!!,

                point2X = tetragon.points[2].x!!,
                point2Y = tetragon.points[2].y!!,
                point2Z = tetragon.points[2].z!!,

                point3X = tetragon.points[3].x!!,
                point3Y = tetragon.points[3].y!!,
                point3Z = tetragon.points[3].z!!,

                ).apply {
                tetragonRepository.save(this)
            }
        }
    }

    private fun processClueEllipses(
        ellipses: List<EllipseFromJson>,
        levelZone: LevelZone,
    ) {
        ellipses.forEach { ellipse ->
            Ellipse(
                inGameId = ellipse.id!!,
                levelZone = levelZone,
                x = ellipse.center!!.x!!,
                y = ellipse.center!!.y!!,
                z = ellipse.center!!.z!!,
                height = ellipse.height!!,
                width = ellipse.width!!,
            ).apply {
                ellipseRepository.save(this)
            }
        }
    }
}