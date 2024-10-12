package com.arkhamusserver.arkhamus.gameplayQuality.items

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.RecipesSource
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ItemType.*
import com.arkhamusserver.arkhamus.view.validator.utils.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RecipeTest() {
    @Autowired
    lateinit var recipesSource: RecipesSource

    @Test
    fun allItemsHaveRecipes() {
        val itemTypesWithRecipes = setOf<ItemType>(
            CRAFT_T2,
            INVESTIGATION,
            USEFUL_ITEM,
            CULTIST_ITEM,
            CORK,
            ADVANCED_USEFUL_ITEM,
            ADVANCED_CULTIST_ITEM,
        )
        val items = Item.values().filter {
            it.itemType in itemTypesWithRecipes
        }
        val recipes = recipesSource.getAllRecipes()
        val itemsWithRecipes = recipes.map { it.item }.toSet()
        val missingItems = items.filter { it !in itemsWithRecipes }
        assertTrue(
            missingItems.isEmpty(),
            "The following items have no recipes: $missingItems",
            relatedObject = "Recipe"
        )
    }

    @Test
    fun allItemsHaveUniqId() {
        val ids = Item.values().map { it.id }
        val count = ids.groupingBy { it }.eachCount()
        val moreThanOne = count.filter { it.value > 1 }
        assertTrue(
            moreThanOne.isEmpty(),
            "The following items have duplicated Ids: $moreThanOne",
            relatedObject = "Item"
        )
    }

    @Test
    fun allRecipesHaveUniqId() {
        val ids = recipesSource.getAllRecipes().map { it.recipeId }
        val count = ids.groupingBy { it }.eachCount()
        val moreThanOne = count.filter { it.value > 1 }
        assertTrue(
            moreThanOne.isEmpty(),
            "The following items have duplicated Ids: $moreThanOne",
            relatedObject = "Item"
        )
    }
}