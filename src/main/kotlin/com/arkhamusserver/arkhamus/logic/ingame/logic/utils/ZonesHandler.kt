package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GameDataLevelZone
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import org.springframework.stereotype.Component

@Component
class ZonesHandler(
    private val geometryUtils: GeometryUtils
) {

    fun filterByPosition(
        withPoint: WithPoint,
        levelGeometryData: LevelGeometryData,
        types: Set<ZoneType> = emptySet()
    ): List<LevelZone> {
        val zones = with(GeometryUtils.Point(withPoint.x(), withPoint.z())) {
            levelGeometryData.zones.filter { zone ->
                zone.ellipses.any { ellipse -> geometryUtils.contains(ellipse, this) } ||
                        zone.tetragons.any { tetragon -> geometryUtils.contains(tetragon, this) }
            }
        }.map {
            LevelZone(
                zoneId = it.zoneId,
                zoneType = it.zoneType
            )
        }
        return if (types.isEmpty()) zones else zones.filter { it.zoneType in types }
    }

    fun inZone(
        withPoint: WithPoint,
        zone: GameDataLevelZone,
    ): Boolean =
        zone.ellipses.any { ellipse -> geometryUtils.contains(ellipse, withPoint) } ||
                zone.tetragons.any { tetragon -> geometryUtils.contains(tetragon, withPoint) }

}