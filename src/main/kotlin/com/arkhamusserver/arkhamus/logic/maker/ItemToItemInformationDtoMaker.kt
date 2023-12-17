package com.arkhamusserver.arkhamus.logic.maker

import com.arkhamusserver.arkhamus.logic.dto.ingame.ItemInformationDto
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import org.springframework.stereotype.Component

@Component
class ItemToItemInformationDtoMaker {
    fun convert(values: List<Item>): List<ItemInformationDto> =
        values.map { convert(it) }

    fun convert(value: Item): ItemInformationDto =
        ItemInformationDto().apply {
            name = value.name
            title = value.name.lowercase()
            type = value.getItemType()
        }


}