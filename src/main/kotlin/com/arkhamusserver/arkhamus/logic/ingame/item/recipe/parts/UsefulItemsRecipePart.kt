package com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.SECOND_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.toRecipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
import org.springframework.stereotype.Component

@Component
class UsefulItemsRecipePart() : RecipeSourcePart {

    companion object {
        val defaultCrafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
    }

    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(
                Ingredient(SAINT_QUARTZ, 3),
                Ingredient(EYE, 1)
            ).toRecipe(
                6001,
                item = PILL,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(RAGS, 3),
                Ingredient(RING, 1)
            ).toRecipe(
                6002,
                item = TOWN_PORTAL_SCROLL,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(ELDER_SIGN, 3),
                Ingredient(BLIGHTING_JEWEL, 2)
            ).toRecipe(
                6003,
                item = VOTE_TOKEN,
                timeToCraft = SECOND_IN_MILLIS * 30,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(CORRUPTED_TOPAZ, 3),
                Ingredient(HIGGS_BOSON, 1)
            ).toRecipe(
                6004,
                item = DISPELL_FLASK,
                timeToCraft = SECOND_IN_MILLIS *15,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(CRYSTALLIZED_BLOOD, 3),
                Ingredient(CLOCK, 1)
            ).toRecipe(
                6005,
                item = SOLARITE,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = defaultCrafterTypes
            ),
        )
    }
}