package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType

data class ItemInformationDto(
    var id: Long? = null,
    var name: Item? = null,
    var title: String? = null,
    var type: ItemType? = null
)