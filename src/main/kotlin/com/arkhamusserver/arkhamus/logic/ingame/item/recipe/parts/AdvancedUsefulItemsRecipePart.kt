package com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.SECOND_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.toRecipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
import org.springframework.stereotype.Component

@Component
class AdvancedUsefulItemsRecipePart() : RecipeSourcePart {

    companion object {
        val defaultCrafterTypes = listOf(CrafterType.ADVANCED)
    }

    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(
                Ingredient(VIOLET_SCROLL, 1),
                Ingredient(BLACK_STONE, 1)
            ).toRecipe(
                9001,
                item = CLOAK_OF_FLAMES,
                timeToCraft = SECOND_IN_MILLIS * 30,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(GREEN_SCROLL, 1),
                Ingredient(BOOK, 1)
            ).toRecipe(
                9002,
                item = INVULNERABILITY_POTION,
                timeToCraft = SECOND_IN_MILLIS * 10,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(BLUE_EGG, 1),
                Ingredient(CLOCK, 1)
            ).toRecipe(
                9003,
                item = TOWN_PORTAL_AMULET,
                timeToCraft = SECOND_IN_MILLIS * 15,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(ORANGE_EGG, 1),
                Ingredient(EYE, 1)
            ).toRecipe(
                9004,
                item = DISPELL_STICK,
                timeToCraft = SECOND_IN_MILLIS * 30,
                crafterTypes = defaultCrafterTypes
            ),
        )
    }
}