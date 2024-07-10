package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.view.dto.netty.request.UserPosition
import org.springframework.stereotype.Component

@Component
class ZonesHandler(
    private val geometryUtils: GeometryUtils
) {
    fun filterByUserPosition(
        userPositionX: Double,
        userPositionY: Double,
        levelGeometryData: LevelGeometryData,
        types: Set<ZoneType> = emptySet()
    ): List<LevelZone> {
        val zones = with(GeometryUtils.Point(userPositionX, userPositionY)) {
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

    fun filterByUserPosition(
        userPosition: UserPosition,
        levelGeometryData: LevelGeometryData
    ): List<LevelZone> {
        return filterByUserPosition(userPosition.x, userPosition.y, levelGeometryData)
    }
}