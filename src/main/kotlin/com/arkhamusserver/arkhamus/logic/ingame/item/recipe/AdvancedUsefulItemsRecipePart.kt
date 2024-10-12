package com.arkhamusserver.arkhamus.logic.ingame.item.recipe

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.SECOND_IN_MILLIS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
import org.springframework.stereotype.Component

@Component
class AdvancedUsefulItemsRecipePart(): RecipeSourcePart {
    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(
                Ingredient(SAINT_QUARTZ, 3),
                Ingredient(EYE, 1)
            ).toRecipe(
                9001,
                item = CLOAK_OF_FLAMES,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(SAINT_QUARTZ, 3),
                Ingredient(EYE, 1)
            ).toRecipe(
                9002,
                item = INVULNERABILITY_POTION,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(SAINT_QUARTZ, 3),
                Ingredient(EYE, 1)
            ).toRecipe(
                9003,
                item = TOWN_PORTAL_AMULET,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),
        )
    }
}