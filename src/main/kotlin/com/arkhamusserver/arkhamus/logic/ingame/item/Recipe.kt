package com.arkhamusserver.arkhamus.logic.ingame.item

data class Recipe(
    var ingredients: List<Ingredient> = emptyList()
)

fun List<Ingredient>.toRecipe() =
    this.let {
        Recipe().apply {
            this.ingredients = it
        }
    }

fun emptyRecipe() = Recipe().apply {
    this.ingredients = emptyList()
}