package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.core.God
import com.arkhamusserver.arkhamus.model.enums.ingame.core.GodType

data class GodDto(
    var id: Int? = null,
    var name: God? = null,
    var types: List<GodType>? = emptyList()
)