package com.arkhamusserver.arkhamus.logic.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType

data class ItemInformationDto(
    var name: String? = null,
    var title: String? = null,
    var type: ItemType? = null
)