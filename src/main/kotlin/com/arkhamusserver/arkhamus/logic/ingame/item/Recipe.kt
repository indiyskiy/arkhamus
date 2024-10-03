package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

data class Recipe(
    var recipeId: Int,
    var item: Item,
    var ingredients: List<Ingredient> = emptyList(),
    var timeToCraft: Long = 0L,
    var numberOfItems: Int = 1,
    var crafterTypes: List<CrafterType> = emptyList()
)

fun List<Ingredient>.toRecipe(
    recipeId: Int,
    item: Item,
    timeToCraft: Long = 0L,
    numberOfItems: Int = 1,
    crafterTypes: List<CrafterType> = emptyList()
) =
    this.let {
        Recipe(recipeId, item).apply {
            this.recipeId = recipeId
            this.ingredients = it
            this.timeToCraft = timeToCraft
            this.numberOfItems = numberOfItems
            this.crafterTypes = crafterTypes
        }
    }
