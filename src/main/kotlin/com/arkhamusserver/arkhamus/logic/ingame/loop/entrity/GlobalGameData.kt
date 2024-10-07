package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import com.arkhamusserver.arkhamus.model.redis.*

data class GlobalGameData(
    val game: RedisGame,
    var altarHolder: RedisAltarHolder?,
    var altarPolling: RedisAltarPolling? = null,
    var altars: Map<Long, RedisAltar> = emptyMap(),
    var users: Map<Long, RedisGameUser> = emptyMap(),
    var clues: List<RedisClue> = emptyList(),
    var containers: Map<Long, RedisContainer> = emptyMap(),
    var crafters: Map<Long, RedisCrafter> = emptyMap(),
    var lanterns: List<RedisLantern> = emptyList(),
    var timeEvents: List<RedisTimeEvent> = emptyList(),
    var shortTimeEvents: List<RedisShortTimeEvent> = emptyList(),
    var castAbilities: List<RedisAbilityCast> = emptyList(),
    var craftProcess: List<RedisCraftProcess> = emptyList(),
    var inBetweenEvents: InBetweenEventHolder = InBetweenEventHolder(),
    var levelGeometryData: LevelGeometryData = LevelGeometryData(),
    var quests: List<RedisQuest> = emptyList(),
    var questRewardsByQuestId: Map<Long, List<RedisQuestReward>> = emptyMap(),
    var questProgressByUserId: Map<Long, List<RedisUserQuestProgress>> = emptyMap(),
    var voteSpots: List<RedisVoteSpot> = emptyList(),
    var userVoteSpotsBySpotId: Map<Long, List<RedisUserVoteSpot>> = emptyMap(),
    var thresholdsByZoneId: Map<Long, List<RedisThreshold>> = emptyMap(),
    var doorsByZoneId: Map<Long, List<RedisDoor>> = emptyMap(),
) {
    fun buildGeometryData(
        zones: List<RedisLevelZone>,
        tetragons: List<RedisLevelZoneTetragon>,
        ellipses: List<RedisLevelZoneEllipse>,
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
        }
    }

    private fun mapTetragons(
        redisLevelZoneTetragons: List<RedisLevelZoneTetragon>
    ): List<GeometryUtils.Tetragon> {
        return redisLevelZoneTetragons.map { tetragon ->
            GeometryUtils.Tetragon(
                p0 = GeometryUtils.Point(tetragon.point0X, tetragon.point0Z),
                p1 = GeometryUtils.Point(tetragon.point1X, tetragon.point1Z),
                p2 = GeometryUtils.Point(tetragon.point2X, tetragon.point2Z),
                p3 = GeometryUtils.Point(tetragon.point3X, tetragon.point3Z),
            )
        }
    }

    private fun mapEllipses(
        redisLevelZoneEllipses: List<RedisLevelZoneEllipse>
    ): List<GeometryUtils.Ellipse> {
        return redisLevelZoneEllipses.map { ellipse ->
            GeometryUtils.Ellipse(
                center = GeometryUtils.Point(ellipse.pointX, ellipse.pointZ),
                rz = ellipse.height / 2,
                rx = ellipse.width / 2
            )
        }
    }

}

data class LevelGeometryData(
    var zones: List<GameDataLevelZone> = emptyList()
)

data class GameDataLevelZone(
    val zoneId: Long,
    val zoneType: ZoneType,
    val tetragons: List<GeometryUtils.Tetragon>,
    val ellipses: List<GeometryUtils.Ellipse>
)

