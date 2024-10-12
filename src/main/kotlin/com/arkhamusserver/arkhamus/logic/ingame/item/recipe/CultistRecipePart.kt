package com.arkhamusserver.arkhamus.logic.ingame.item.recipe

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.BLIGHTING_JEWEL
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORRUPTED_TOPAZ
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CRYSTALLIZED_BLOOD
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CURSED_POTATO
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.DARK_ESSENCE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.MOON_STONE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.RITUAL_DAGGER
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.SAINT_QUARTZ
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.STRANGE_BONE
import org.springframework.stereotype.Component
import kotlin.math.roundToLong

@Component
class CultistRecipePart() : RecipeSourcePart {
    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(Ingredient(SAINT_QUARTZ, 5), Ingredient(DARK_ESSENCE, 3)).toRecipe(
                7001,
                item = MOON_STONE,
                timeToCraft = MINUTE_IN_MILLIS,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),

            listOf(Ingredient(CRYSTALLIZED_BLOOD, 3), Ingredient(STRANGE_BONE, 1)).toRecipe(
                7002,
                item = CURSED_POTATO,
                timeToCraft = (0.5 * MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 1,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
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
                crafterTypes = listOf(CrafterType.CULTIST)
            ),
        )
    }
}