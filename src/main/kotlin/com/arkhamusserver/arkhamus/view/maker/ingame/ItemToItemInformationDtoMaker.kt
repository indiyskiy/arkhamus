package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.view.dto.ingame.ItemInformationDto
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import org.springframework.stereotype.Component

@Component
class ItemToItemInformationDtoMaker {
    fun convert(values: List<Item>): List<ItemInformationDto> =
        values.map { convert(it) }

    fun convert(value: Item): ItemInformationDto =
        ItemInformationDto().apply {
            name = value
            title = value.name.lowercase()
            type = value.getItemType()
        }


}