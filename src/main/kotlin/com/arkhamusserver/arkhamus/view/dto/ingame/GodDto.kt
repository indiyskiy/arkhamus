package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.God

data class GodDto(
    var name: God? = null,
    var title: String? = null,
    var types: List<String>? = emptyList()
)