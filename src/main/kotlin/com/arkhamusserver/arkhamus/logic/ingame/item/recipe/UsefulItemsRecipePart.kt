package com.arkhamusserver.arkhamus.logic.ingame.item.recipe

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.SECOND_IN_MILLIS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
import org.springframework.stereotype.Component

@Component
class UsefulItemsRecipePart(): RecipeSourcePart {
    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(
                Ingredient(SAINT_QUARTZ, 3),
                Ingredient(EYE, 1)
            ).toRecipe(
                6001,
                item = VEIL,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(RAGS, 3),
                Ingredient(RING, 1)
            ).toRecipe(
                6002,
                item = TOWN_PORTAL_SCROLL,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(ELDER_SIGN, 3),
                Ingredient(MASK, 1)
            ).toRecipe(
                6003,
                item = VOTE_TOKEN,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(CORRUPTED_TOPAZ, 3),
                Ingredient(BLACK_STONE, 1)
            ).toRecipe(
                6004,
                item = SCIENTIFIC_GIZMO,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(CRYSTALLIZED_BLOOD, 3),
                Ingredient(CLOCK, 1)
            ).toRecipe(
                6005,
                item = SOLARITE,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),
        )
    }
}