package com.arkhamusserver.arkhamus.view.levelDesign

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType

data class LevelFromJson(
    var levelId: Long? = null,
    var levelVersion: Long? = null,
    var levelHeight: Long? = null,
    var levelWidth: Long? = null,
    var containers: List<ContainerFromJson> = emptyList(),
    var lanterns: List<LanternFromJson> = emptyList(),
    var altars: List<AltarFromJson> = emptyList(),
    var ritualAreas: List<RitualAreaFromJson> = emptyList(),
    var startMarkers: List<JsonStartMarker> = emptyList(),
    var crafters: List<CrafterFromJson> = emptyList(),
    var clueZones: List<ClueZoneFromJson> = emptyList()
)

data class ContainerFromJson(
    var id: Long? = null,
    var interactionRadius: Double? = null,
    var x: Double? = null,
    var y: Double? = null
)

data class LanternFromJson(
    var id: Long? = null,
    var lightRange: Double? = null,
    var x: Double? = null,
    var y: Double? = null
)

data class AltarFromJson(
    var id: Long? = null,
    var interactionRadius: Double? = null,
    var x: Double? = null,
    var y: Double? = null
)

data class RitualAreaFromJson(
    var id: Long? = null,
    var radius: Double? = null,
    var x: Double? = null,
    var y: Double? = null
)

data class JsonStartMarker(
    var x: Double? = null,
    var y: Double? = null
)

data class CrafterFromJson(
    var id: Long? = null,
    var crafterType: CrafterType? = null,
    var interactionRadius: Double? = null,
    var x: Double? = null,
    var y: Double? = null
)

data class ClueZoneFromJson(
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
)