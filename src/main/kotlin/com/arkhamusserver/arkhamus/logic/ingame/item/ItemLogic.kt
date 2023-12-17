package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.logic.dto.ingame.ItemInformationDto
import com.arkhamusserver.arkhamus.logic.maker.ItemToItemInformationDtoMaker
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import org.springframework.stereotype.Component

@Component
class ItemLogic(
    private val dtoMaker: ItemToItemInformationDtoMaker
) {
    fun listAllItems(): List<ItemInformationDto>? =
        dtoMaker.convert(Item.values().toList())


}