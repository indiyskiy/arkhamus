package com.arkhamusserver.arkhamus.view.levelDesign

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType

data class CrafterFromJson(
    var id: Long? = null,
    var crafterType: CrafterType? = null,
    var interactionRadius: Double? = null,
    var x: Double? = null,
    var y: Double? = null
)