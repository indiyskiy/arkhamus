package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.LevelGeometryData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.view.dto.netty.request.UserPosition
import org.springframework.stereotype.Component

@Component
class ZonesHandler(
    private val geometryUtils: GeometryUtils
) {
    fun filterByUserPosition(
        userPosition: UserPosition,
        levelGeometryData: LevelGeometryData
    ): List<LevelZone> {
        val zones = with(GeometryUtils.Point(userPosition.x, userPosition.y)) {
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
        return zones
    }
}