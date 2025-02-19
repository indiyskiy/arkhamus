package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.logic.admin.NiceColor


data class AdminGameLevelGeometryDto(
    var levelId: Long,
    var height: Int,
    var width: Int,
    var banZones: ZoneDataDto,
    var soundClueZones: ZoneDataDto,
    var auraClueZones: ZoneDataDto,
    var keyPoints: List<PointDto>,
    var questGivers: List<NpcDto>,
    var tasks: List<TaskGeometryDto>,
    var voteSpots: List<VoteSpotDto>,
    var doors: List<DoorDto>,
    var thresholds: List<ThresholdDto>,
)

data class ZoneDataDto(
    val polygons: List<PolygonDto>,
    val ellipses: List<EllipseDto>,
)

data class PolygonDto(
    val points: List<PointDto>,
    val polygonPoints: String = points.joinToString(" ") { "${it.pointX},${it.pointY}" },
    var color: NiceColor
)

data class EllipseDto(
    var cx: Float, var cy: Float,
    var rx: Float, var ry: Float,
    var color: NiceColor
)

data class NpcDto(
    val points: List<PointDto>,
    val polygonPoints: String = points.joinToString(" ") { "${it.pointX},${it.pointY}" },
    var color: NiceColor
)

data class TaskGeometryDto(
    val points: List<PointDto>,
    val polygonPoints: String = points.joinToString(" ") { "${it.pointX},${it.pointY}" },
    var color: NiceColor
)

data class VoteSpotDto(
    val points: List<PointDto>,
    val polygonPoints: String = points.joinToString(" ") { "${it.pointX},${it.pointY}" },
    var color: NiceColor
)

data class DoorDto(
    val points: List<PointDto>,
    val polygonPoints: String = points.joinToString(" ") { "${it.pointX},${it.pointY}" },
    var color: NiceColor
)

data class ThresholdDto(
    val points: List<PointDto>,
    val polygonPoints: String = points.joinToString(" ") { "${it.pointX},${it.pointY}" },
    var color: NiceColor
)