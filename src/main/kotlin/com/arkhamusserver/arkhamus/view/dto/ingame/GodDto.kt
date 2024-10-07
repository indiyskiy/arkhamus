package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God

data class GodDto(
    var id: Int? = null,
    var name: God? = null,
    var clues: List<Clue>? = emptyList()
)