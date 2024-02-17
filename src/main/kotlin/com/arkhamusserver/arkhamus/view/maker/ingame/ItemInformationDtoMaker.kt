package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.globalutils.capitalizeIfFirstCharIsLowercase
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.view.dto.ingame.ItemInformationDto
import org.springframework.stereotype.Component

@Component
class ItemInformationDtoMaker {
    fun convert(values: List<Item>): List<ItemInformationDto> =
        values.map { convert(it) }

    fun convert(value: Item): ItemInformationDto =
        ItemInformationDto().apply {
            id = value.getId()
            name = value
            title = value.name.lowercase().replace("_", " ")
                .capitalizeIfFirstCharIsLowercase()
            type = value.getItemType()
        }


}