package com.arkhamusserver.arkhamus.view.dto.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType

data class ItemInformationDto(
    var id: Int,
    var name: Item,
    var type: ItemType
)