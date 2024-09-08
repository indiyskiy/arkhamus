package com.arkhamusserver.arkhamus.view.levelDesign

import com.arkhamusserver.arkhamus.model.enums.ingame.ContainerTag

data class ContainerFromJson(
    var id: Long? = null,
    var interactionRadius: Double? = null,
    var containerTags: List<ContainerTag>? = null,
    var x: Double? = null,
    var y: Double? = null,
    var z: Double? = null,
)