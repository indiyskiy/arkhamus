package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.GodType

data class GodDto(
    var id: Int? = null,
    var name: God? = null,
    var title: String? = null,
    var types: List<GodType>? = emptyList()
)