package com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.toRecipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.BLIGHTING_JEWEL
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.BLUE_EGG
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORRUPTED_TOPAZ
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CRYSTALLIZED_BLOOD
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.ELDER_SIGN
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.GREEN_EGG
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.GREEN_SCROLL
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.HIGGS_BOSON
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.ORANGE_EGG
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.RAGS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.SAINT_QUARTZ
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.VIOLET_SCROLL
import org.springframework.stereotype.Component

@Component
class CraftT2RecipePart() : RecipeSourcePart {

    companion object{
        private val defaultCrafterSet = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
    }

    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(
                Ingredient(SAINT_QUARTZ, 2),
                Ingredient(BLIGHTING_JEWEL, 3)
            ).toRecipe(
                4001,
                item = VIOLET_SCROLL,
                timeToCraft = MINUTE_IN_MILLIS / 5,
                crafterTypes = defaultCrafterSet
            ),
            listOf(
                Ingredient(ELDER_SIGN, 2),
                Ingredient(RAGS, 3)
            ).toRecipe(
                4002,
                item = GREEN_SCROLL,
                timeToCraft = MINUTE_IN_MILLIS / 5,
                crafterTypes = defaultCrafterSet
            ),
            listOf(
                Ingredient(HIGGS_BOSON, 2),
                Ingredient(CRYSTALLIZED_BLOOD, 3)
            ).toRecipe(
                4003,
                item = BLUE_EGG,
                timeToCraft = MINUTE_IN_MILLIS / 5,
                crafterTypes = defaultCrafterSet
            ),
            listOf(
                Ingredient(ELDER_SIGN, 2),
                Ingredient(CORRUPTED_TOPAZ, 3)
            ).toRecipe(
                4004,
                item = ORANGE_EGG,
                timeToCraft = MINUTE_IN_MILLIS / 5,
                crafterTypes = defaultCrafterSet
            ),
            listOf(
                Ingredient(SAINT_QUARTZ, 2),
                Ingredient(HIGGS_BOSON, 3)
            ).toRecipe(
                4005,
                item = GREEN_EGG,
                timeToCraft = MINUTE_IN_MILLIS / 5,
                crafterTypes = defaultCrafterSet
            ),
        )
    }
}