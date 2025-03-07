package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GeometryUtils
import com.arkhamusserver.arkhamus.model.ingame.*

data class GlobalGameData(
    val game: InRamGame,
    var timeEvents: List<InGameTimeEvent> = emptyList(),
    var shortTimeEvents: List<InGameShortTimeEvent> = emptyList(),
    var castAbilities: List<InGameAbilityCast> = emptyList(),
    var inBetweenEvents: InBetweenEventHolder = InBetweenEventHolder(),

    var users: Map<Long, InGameUser> = emptyMap(),

    var altarHolder: InGameAltarHolder?,
    var altarPolling: InGameAltarPolling? = null,
    var altars: Map<Long, InGameAltar> = emptyMap(),

    var clues: CluesContainer = CluesContainer(
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList(),
        emptyList()
    ),

    var containers: Map<Long, InGameContainer> = emptyMap(),
    var crafters: Map<Long, InGameCrafter> = emptyMap(),
    var lanterns: List<InGameLantern> = emptyList(),

    var craftProcess: List<InGameCraftProcess> = emptyList(),
    var levelGeometryData: LevelGeometryData = LevelGeometryData(),

    var quests: List<InGameQuest> = emptyList(),
    var questRewardsByQuestProgressId: Map<String, List<InGameQuestReward>> = emptyMap(),
    var questProgressByUserId: Map<Long, List<InGameUserQuestProgress>> = emptyMap(),
    var questGivers: List<InGameQuestGiver> = emptyList(),

    var voteSpots: List<InGameVoteSpot> = emptyList(),
    var userVoteSpotsBySpotId: Map<Long, List<InGameUserVoteSpot>> = emptyMap(),
    var thresholds: List<InGameThreshold> = emptyList(),
    var doorsByZoneId: Map<Long, List<InGameDoor>> = emptyMap(),
) {
    fun buildGeometryData(
        zones: List<InGameLevelZone>,
        tetragons: List<InGameLevelZoneTetragon>,
        ellipses: List<InGameLevelZoneEllipse>,
        inGameVisibilityMap: InGameVisibilityMap
    ): LevelGeometryData {
        val tetragonsMap = tetragons.groupBy { it.levelZoneId }
        val ellipsesMap = ellipses.groupBy { it.levelZoneId }
        val zonesMap = zones.map { zone ->
            GameDataLevelZone(
                zoneId = zone.inGameId(),
                zoneType = zone.zoneType,
                tetragons = mapTetragons(tetragonsMap[zone.inGameId()] ?: emptyList()),
                ellipses = mapEllipses(ellipsesMap[zone.inGameId()] ?: emptyList())
            )
        }
        return LevelGeometryData().apply {
            this.zones = zonesMap
            this.visibilityMap = inGameVisibilityMap.visibilityMap
        }
    }

    private fun mapTetragons(
        inGameLevelZoneTetragons: List<InGameLevelZoneTetragon>
    ): List<GeometryUtils.Tetragon> {
        return inGameLevelZoneTetragons.map { tetragon ->
            GeometryUtils.Tetragon(
                p0 = GeometryUtils.Point(tetragon.point0X, tetragon.point0Z),
                p1 = GeometryUtils.Point(tetragon.point1X, tetragon.point1Z),
                p2 = GeometryUtils.Point(tetragon.point2X, tetragon.point2Z),
                p3 = GeometryUtils.Point(tetragon.point3X, tetragon.point3Z),
            )
        }
    }

    private fun mapEllipses(
        inGameLevelZoneEllips: List<InGameLevelZoneEllipse>
    ): List<GeometryUtils.Ellipse> {
        return inGameLevelZoneEllips.map { ellipse ->
            GeometryUtils.Ellipse(
                center = GeometryUtils.Point(ellipse.pointX, ellipse.pointZ),
                rz = ellipse.height / 2,
                rx = ellipse.width / 2
            )
        }
    }

}

