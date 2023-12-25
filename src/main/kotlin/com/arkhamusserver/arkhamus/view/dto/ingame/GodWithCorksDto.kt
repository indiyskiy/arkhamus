package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.Item

data class GodWithCorksDto(
    var god: God? = null,
    var cork: Item? = null,
)