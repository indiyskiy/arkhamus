package com.arkhamusserver.arkhamus.logic.ingame.item.recipe

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.AURA_INVESTIGATION_ITEM
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.BLACK_STONE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.BLIGHTING_JEWEL
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORRUPTED_TOPAZ
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORRUPTION_INVESTIGATION_ITEM
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CRYSTALLIZED_BLOOD
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.DISTORTION_INVESTIGATION_ITEM
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.ELDER_SIGN
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.EYE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.HIGGS_BOSON
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.INSCRIPTION_INVESTIGATION_ITEM
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.MASK
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.OMEN_INVESTIGATION_ITEM
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.PLATE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.RAGS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.RING
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.SAINT_QUARTZ
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.SCENT_INVESTIGATION_ITEM
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.SCYTHE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.SOUND_INVESTIGATION_ITEM
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.TEAR
import org.springframework.stereotype.Component

@Component
class InvestigationRecipePart(): RecipeSourcePart {
    override fun recipes(): List<Recipe> {
        return listOf(
            listOf(
                Ingredient(SCYTHE, 1),
                Ingredient(RAGS, 5)
            ).toRecipe(
                5001,
                item = INSCRIPTION_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(BLACK_STONE, 1),
                Ingredient(HIGGS_BOSON, 5)
            ).toRecipe(
                5002,
                item = SOUND_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(RING, 1),
                Ingredient(CORRUPTED_TOPAZ, 5)
            ).toRecipe(
                5003,
                item = SCENT_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(TEAR, 1),
                Ingredient(ELDER_SIGN, 5)
            ).toRecipe(
                5004,
                item = AURA_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(PLATE, 1),
                Ingredient(CRYSTALLIZED_BLOOD, 5)
            ).toRecipe(
                5005,
                item = CORRUPTION_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(EYE, 1),
                Ingredient(SAINT_QUARTZ, 5)
            ).toRecipe(
                5006,
                item = OMEN_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(MASK, 1),
                Ingredient(BLIGHTING_JEWEL, 5)
            ).toRecipe(
                5007,
                item = DISTORTION_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
        )
    }
}