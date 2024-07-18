package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.logic.admin.NiceColor


data class AdminGameLevelGeometryDto(
    var levelId: Long,
    var height: Int,
    var width: Int,
    var polygons: List<PolygonDto>,
    var ellipses: List<EllipseDto>,
    var keyPoints: List<PointDto>,
    var questGivers: List<NpcDto>,
    var tasks: List<TaskGeometryDto>,
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

data class PointDto(
    var pointX: Float, var pointY: Float, var color: NiceColor
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