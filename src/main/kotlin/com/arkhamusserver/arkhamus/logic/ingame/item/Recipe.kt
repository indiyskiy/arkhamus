package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.Item

data class Recipe(
    var recipeId: Int,
    var item: Item,
    var ingredients: List<Ingredient> = emptyList(),
    var timeToCraft: Long = 0L,
    var numberOfItems: Long = 1L,
    var crafterTypes: List<CrafterType> = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
)

fun List<Ingredient>.toRecipe(
    recipeId: Int,
    item: Item,
    timeToCraft: Long = 0L,
    numberOfItems: Long = 1L,
    crafterTypes: List<CrafterType> = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
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
