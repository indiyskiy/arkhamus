package com.arkhamusserver.arkhamus.logic.maker.ingame

import com.arkhamusserver.arkhamus.logic.dto.ingame.IngredientDto
import com.arkhamusserver.arkhamus.logic.ingame.item.Ingredient
import org.springframework.stereotype.Component

@Component
class IngredientToIngredientDtoMaker {
    fun convert(from: List<Ingredient>?): List<IngredientDto> =
        from?.map { convert(it) } ?: emptyList()

    fun convert(from: Ingredient): IngredientDto =
        IngredientDto().apply {
            this.number = from.number
            this.item = from.item
        }


}