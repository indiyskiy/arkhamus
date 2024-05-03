package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.view.dto.ingame.RecipeDto
import org.springframework.stereotype.Component

@Component
class RecipeToRecipeDtoMaker(
    private val ingredientDtoMaker: IngredientDtoMaker,
    private val itemInformationDtoMaker: ItemInformationDtoMaker
) {
    fun convert(from: List<Recipe>): List<RecipeDto> =
        from.map { convert(it) }

    fun convert(from: Recipe): RecipeDto =
        RecipeDto().apply {
            this.item = itemInformationDtoMaker.convert(from.item)
            this.ingredients = ingredientDtoMaker.convert(from.ingredients)
            this.numberOfItems = from.numberOfItems
            this.timeToCraft = from.timeToCraft
            this.crafterTypes = from.crafterTypes
        }

}