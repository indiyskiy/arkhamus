package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.view.dto.ingame.IngredientDto
import com.arkhamusserver.arkhamus.logic.ingame.item.Ingredient
import org.springframework.stereotype.Component

@Component
class IngredientDtoMaker(
    private val itemInformationDtoMaker: ItemInformationDtoMaker
) {
    fun convert(from: List<Ingredient>?): List<IngredientDto> =
        from?.map { convert(it) } ?: emptyList()

    fun convert(from: Ingredient): IngredientDto =
        IngredientDto().apply {
            this.number = from.number
            this.item = from.item?.let { itemInformationDtoMaker.convert(it) }
        }

}