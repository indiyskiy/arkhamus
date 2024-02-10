package com.arkhamusserver.arkhamus.view.levelDesign

data class LevelFromJson (
    var levelId: Long? = null,
    var levelVersion: Long? = null,
    var levelHeight: Long? = null,
    var levelWidth: Long? = null,
    var containers: List<ContainerFromJson> = emptyList(),
    var startMarkers: List<JsonStartMarker> = emptyList()
)

data class ContainerFromJson(
    var id: Long? = null,
    var interactionRadius: Double? = null,
    var x: Double? = null,
    var y: Double? = null
)
data class JsonStartMarker(
    var x: Double? = null,
    var y: Double? = null
)