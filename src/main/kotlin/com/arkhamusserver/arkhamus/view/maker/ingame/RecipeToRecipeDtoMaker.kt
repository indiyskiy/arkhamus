package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
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
        RecipeDto(
            recipeId = from.recipeId,
            item = itemInformationDtoMaker.convert(from.item),
            ingredients = ingredientDtoMaker.convert(from.ingredients),
            numberOfItems = from.numberOfItems,
            timeToCraft = from.timeToCraft,
            crafterTypes = from.crafterTypes,
        )

}