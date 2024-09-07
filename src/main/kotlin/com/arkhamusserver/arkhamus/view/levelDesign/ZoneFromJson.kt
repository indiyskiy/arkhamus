package com.arkhamusserver.arkhamus.view.levelDesign

data class ZoneFromJson(
    var zoneId: Long? = null,
    var tetragons: List<TetragonFromJson> = emptyList(),
    var ellipses: List<EllipseFromJson> = emptyList(),
)

data class TetragonFromJson(
    var id: Long? = null,
    var points: List<PointJson> = emptyList(),
)

data class EllipseFromJson(
    var id: Long? = null,
    var center: PointJson? = null,
    var height: Double? = null,
    var width: Double? = null,
)

data class PointJson(
    var x: Double? = null,
    var y: Double? = null,
    var z: Double? = null,
)