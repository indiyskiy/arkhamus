package com.arkhamusserver.arkhamus.view.levelDesign

data class LevelFromJson (
    var levelId: Long? = null,
    var levelVersion: Long? = null,
    var containers: List<ContainerFromJson> = emptyList()
)

data class ContainerFromJson(
    var id: Long? = null,
    var interactionRadius: Double? = null,
    var x: Double? = null,
    var y: Double? = null
)