package com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.toRecipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import org.springframework.stereotype.Component
import kotlin.math.roundToLong

@Component
class AdvancedCultistRecipePart() : RecipeSourcePart {
    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(
                Ingredient(Item.BLACK_STONE, 1),
                Ingredient(Item.SOUL_STONE, 1)
            ).toRecipe(
                10001,
                item = Item.BONE_CHIMES,
                timeToCraft = (0.7 * GlobalGameSettings.Companion.MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = listOf(CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(Item.BOOK, 1),
                Ingredient(Item.DARK_ESSENCE, 1)
            ).toRecipe(
                10002,
                item = Item.PIPE_OF_INSIGHT,
                timeToCraft = (0.7 * GlobalGameSettings.Companion.MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = listOf(CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(Item.CLOCK, 1),
                Ingredient(Item.STRANGE_BONE, 1)
            ).toRecipe(
                10003,
                item = Item.CIRCLET_OF_NOBILITY,
                timeToCraft = (0.7 * GlobalGameSettings.Companion.MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = listOf(CrafterType.CULTIST)
            ),
            listOf(
                Ingredient(Item.EYE, 1),
                Ingredient(Item.INNSMOUTH_WATTER, 1)
            ).toRecipe(
                10004,
                item = Item.DUST_OF_DISAPPEARANCE,
                timeToCraft = (0.7 * GlobalGameSettings.Companion.MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = listOf(CrafterType.CULTIST)
            ),
        ).sortedBy { it.item.name }.sortedBy { it.item.itemType }
    }

}