package com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.toRecipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
import org.springframework.stereotype.Component

@Component
class InvestigationRecipePart() : RecipeSourcePart {

    companion object {
        val defaultCrafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
    }

    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(
                Ingredient(SCYTHE, 1),
                Ingredient(RAGS, 5)
            ).toRecipe(
                5001,
                item = INSCRIPTION_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(TEAR, 1),
                Ingredient(ELDER_SIGN, 5)
            ).toRecipe(
                5004,
                item = AURA_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(PLATE, 1),
                Ingredient(CRYSTALLIZED_BLOOD, 5)
            ).toRecipe(
                5005,
                item = CORRUPTION_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(MASK, 1),
                Ingredient(BLIGHTING_JEWEL, 5)
            ).toRecipe(
                5007,
                item = DISTORTION_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = defaultCrafterTypes
            ),
            //2.0
            listOf(
                Ingredient(RING, 1),
                Ingredient(CORRUPTED_TOPAZ, 5)
            ).toRecipe(
                50001,
                item = INNOVATE_SCENT_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(BLACK_STONE, 1),
                Ingredient(HIGGS_BOSON, 5)
            ).toRecipe(
                50002,
                item = INNOVATE_SOUND_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = defaultCrafterTypes
            ),
            listOf(
                Ingredient(EYE, 1),
                Ingredient(SAINT_QUARTZ, 5)
            ).toRecipe(
                50003,
                item = INNOVATE_OMEN_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = defaultCrafterTypes
            ),
        )
    }
}