package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.view.dto.ingame.IngredientDto
import org.springframework.stereotype.Component

@Component
class IngredientDtoMaker(
    private val itemInformationDtoMaker: ItemInformationDtoMaker
) {
    fun convert(from: List<Ingredient>): List<IngredientDto> =
        from.map { convert(it) }

    fun convert(from: Ingredient): IngredientDto =
        IngredientDto(
            number = from.number,
            item = itemInformationDtoMaker.convert(from.item)
        )

}