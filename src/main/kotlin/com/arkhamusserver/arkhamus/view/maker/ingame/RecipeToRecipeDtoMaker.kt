package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.view.dto.ingame.RecipeDto
import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import org.springframework.stereotype.Component

@Component
class RecipeToRecipeDtoMaker(
    private val ingredientToIngredientDtoMaker: IngredientToIngredientDtoMaker
) {
    fun convert(from: List<Data>): List<RecipeDto> =
        from.map { convert(it) }

    fun convert(from: Data): RecipeDto =
        RecipeDto().apply {
            this.item = from.item
            this.ingredients = ingredientToIngredientDtoMaker.convert(from.recipe.ingredients)
        }

    data class Data(
        val item: Item,
        val recipe: Recipe
    )

}