package com.arkhamusserver.arkhamus.logic.ingame.item.recipe

import org.springframework.stereotype.Component

@Component
class RecipesSource(
    private val recipeSourceParts: List<RecipeSourcePart>
) {
    private val allRecipes = buildAll()
    private val allRecipesMap = allRecipes.associateBy { it.recipeId }

    fun getAllRecipes() = allRecipes
    fun byId(id: Int) = allRecipesMap[id]

    private fun buildAll(): List<Recipe> {
        return recipeSourceParts
            .map { it.recipes() }
            .flatten()
            .sortedBy { it.item.name }
            .sortedBy { it.item.itemType }
    }
}

