package com.arkhamusserver.arkhamus.logic.ingame.item.recipe

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts.RecipeSourcePart
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
import org.springframework.stereotype.Component
import kotlin.math.roundToLong

@Component
class AdvancedCultistRecipePart() : RecipeSourcePart {
    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(
                Ingredient(BLACK_STONE, 1),
                Ingredient(SOUL_STONE, 1)
            ).toRecipe(
                10001,
                item = BONE_CHIMES,
                timeToCraft = (0.7 * MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = listOf(CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(BOOK, 1),
                Ingredient(DARK_ESSENCE, 1)
            ).toRecipe(
                10002,
                item = PIPE_OF_INSIGHT,
                timeToCraft = (0.7 * MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = listOf(CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(CLOCK, 1),
                Ingredient(STRANGE_BONE, 1)
            ).toRecipe(
                10003,
                item = CIRCLET_OF_NOBILITY,
                timeToCraft = (0.7 * MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = listOf(CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(EYE, 1),
                Ingredient(SOUL_STONE, 1)
            ).toRecipe(
                10004,
                item = DUST_OF_DISAPPEARANCE,
                timeToCraft = (0.7 * MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = listOf(CrafterType.CULTIST)
            ),
        ).sortedBy { it.item.name }.sortedBy { it.item.itemType }
    }

}