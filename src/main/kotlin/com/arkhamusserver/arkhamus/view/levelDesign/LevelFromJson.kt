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
    var startMarkers: List<JsonStartMarker> = emptyList(),
    var crafters: List<CrafterFromJson> = emptyList()
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