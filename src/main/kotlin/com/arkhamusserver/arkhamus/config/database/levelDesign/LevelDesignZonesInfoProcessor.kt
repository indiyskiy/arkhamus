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
import org.postgresql.geometric.PGpoint
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
                point0 = PGpoint(tetragon.points[0].x!!, tetragon.points[0].y!!),
                point1 = PGpoint(tetragon.points[1].x!!, tetragon.points[1].y!!),
                point2 = PGpoint(tetragon.points[2].x!!, tetragon.points[2].y!!),
                point3 = PGpoint(tetragon.points[3].x!!, tetragon.points[3].y!!),
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
                point = PGpoint(ellipse.center!!.x!!, ellipse.center!!.y!!),
                height = ellipse.height!!,
                width = ellipse.width!!,
            ).apply {
                ellipseRepository.save(this)
            }
        }
    }
}