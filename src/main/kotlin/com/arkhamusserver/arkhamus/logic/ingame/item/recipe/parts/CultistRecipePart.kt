package com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.toRecipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
import org.springframework.stereotype.Component
import kotlin.math.roundToLong

@Component
class CultistRecipePart() : RecipeSourcePart {

    companion object {
        val defaultCrafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
    }

    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(Ingredient(SAINT_QUARTZ, 5), Ingredient(DARK_ESSENCE, 3)).toRecipe(
                7001,
                item = MOON_STONE,
                timeToCraft = MINUTE_IN_MILLIS,
                crafterTypes = defaultCrafterTypes
            ),

            listOf(Ingredient(CRYSTALLIZED_BLOOD, 3), Ingredient(STRANGE_BONE, 1)).toRecipe(
                7002,
                item = CURSED_POTATO,
                timeToCraft = (0.5 * MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(CORRUPTED_TOPAZ, 1),
                Ingredient(BLIGHTING_JEWEL, 1),
                Ingredient(STRANGE_BONE, 1)
            ).toRecipe(
                7003,
                item = RITUAL_DAGGER,
                timeToCraft = (0.2 * MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(HIGGS_BOSON, 3),
                Ingredient(SOUL_STONE, 2)
            ).toRecipe(
                7004,
                item = ANNOYING_BELL,
                timeToCraft = MINUTE_IN_MILLIS,
                numberOfItems = 1,
                crafterTypes = defaultCrafterTypes
            ),
        )
    }
}