package com.arkhamusserver.arkhamus.config.database.levelDesign.subprocessors

import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.EllipseRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.LevelZoneRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.ingame.geometry.TetragonRepository
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Ellipse
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelZone
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Tetragon
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.view.levelDesign.EllipseFromJson
import com.arkhamusserver.arkhamus.view.levelDesign.TetragonFromJson
import com.arkhamusserver.arkhamus.view.levelDesign.ZoneFromJson
import com.arkhamusserver.arkhamus.view.validator.utils.assertTrue
import org.springframework.stereotype.Component

@Component
class LevelDesignZonesInfoProcessor(
    private val levelZoneRepository: LevelZoneRepository,
    private val tetragonRepository: TetragonRepository,
    private val ellipseRepository: EllipseRepository,
) {
    fun processZones(
        banZones: List<ZoneFromJson>,
        soundZones: List<ZoneFromJson>,
        auraZones: List<ZoneFromJson>,
        level: Level
    ): List<LevelZone> {
        val banZone = processBanZones(banZones, level)
        val soundZone = processSoundZones(soundZones, level)
        val auraZone = processAuraZones(auraZones, level)
        return  banZone + soundZone + auraZone
    }


    private fun processBanZones(clueZones: List<ZoneFromJson>, savedLevel: Level): List<LevelZone> {
        return clueZones.map { clueZone ->
            val banZone = LevelZone(
                inGameId = clueZone.zoneId!!,
                zoneType = ZoneType.BAN,
                level = savedLevel,
            ).apply {
                levelZoneRepository.save(this)
            }
            processClueTetragons(clueZone.tetragons, banZone)
            processClueEllipses(clueZone.ellipses, banZone)
            banZone
        }
    }

    private fun processSoundZones(soundZones: List<ZoneFromJson>, savedLevel: Level): List<LevelZone> {
        return soundZones.map { soundZone ->
            val banZone = LevelZone(
                inGameId = soundZone.zoneId!!,
                zoneType = ZoneType.SOUND,
                level = savedLevel,
            ).apply {
                levelZoneRepository.save(this)
            }
            processClueTetragons(soundZone.tetragons, banZone)
            processClueEllipses(soundZone.ellipses, banZone)
            banZone
        }
    }

    private fun processAuraZones(soundZones: List<ZoneFromJson>, savedLevel: Level): List<LevelZone> {
        return soundZones.map { soundZone ->
            val banZone = LevelZone(
                inGameId = soundZone.zoneId!!,
                zoneType = ZoneType.AURA,
                level = savedLevel,
            ).apply {
                levelZoneRepository.save(this)
            }
            processClueTetragons(soundZone.tetragons, banZone)
            processClueEllipses(soundZone.ellipses, banZone)
            banZone
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