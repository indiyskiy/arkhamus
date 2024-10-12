package com.arkhamusserver.arkhamus.logic.ingame.item.recipe

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.BLACK_STONE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.BLUE_EGG
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.BOOK
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CLOCK
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_AAMON
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_BELETH
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_BHOLES
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_COLOUR_OUT_OF_SPACE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_CTHULHU
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_CYBELE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_CZEOTHOQUA
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_DAGON
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_GREEN_FLAME
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_KING_IN_YELLOW
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_MI_GO
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_NAMELESS_WINDS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_NINGISHZIDA
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_NYARLATHOTEP
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_PNAKOTIC_HORRORS
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_RED_MASK
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_SHUB_NIGGURATH
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_TZONTEMOC
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_YERLEG
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.CORK_YOG_SOTHOTH
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.EYE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.GREEN_EGG
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.GREEN_SCROLL
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.MASK
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.ORANGE_EGG
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.PLATE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.RING
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.SCYTHE
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.TEAR
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.VIOLET_SCROLL
import org.springframework.stereotype.Component
import kotlin.collections.plus

@Component
class CorkRecipePart() : RecipeSourcePart {
    override fun recipes(): List<Recipe> {
        return listOf(
            (oneOfEach(BOOK, CLOCK, MASK) + Ingredient(VIOLET_SCROLL, 3)).toRecipe(
                8001, item = CORK_AAMON,
            ),

            (oneOfEach(EYE, PLATE, SCYTHE) + Ingredient(GREEN_SCROLL, 3)).toRecipe(
                8002, item = CORK_BELETH,
            ),

            (oneOfEach(MASK, PLATE, TEAR) + Ingredient(BLUE_EGG, 3)).toRecipe(
                8003, item = CORK_BHOLES,
            ),

            (oneOfEach(CLOCK, EYE, TEAR) + Ingredient(ORANGE_EGG, 3)).toRecipe(
                8004, item = CORK_COLOUR_OUT_OF_SPACE,
            ),

            (oneOfEach(CLOCK, MASK, PLATE) + Ingredient(GREEN_EGG, 3)).toRecipe(
                8005, item = CORK_CTHULHU,
            ),

            (oneOfEach(BLACK_STONE, BOOK, PLATE) + Ingredient(VIOLET_SCROLL, 3)).toRecipe(
                8006, item = CORK_CYBELE,
            ),

            (oneOfEach(BLACK_STONE, PLATE, RING) + Ingredient(GREEN_SCROLL, 3)).toRecipe(
                8007, item = CORK_CZEOTHOQUA,
            ),

            (oneOfEach(BLACK_STONE, MASK, SCYTHE) + Ingredient(BLUE_EGG, 3)).toRecipe(
                8008, item = CORK_DAGON,
            ),

            (oneOfEach(BLACK_STONE, EYE, SCYTHE) + Ingredient(ORANGE_EGG, 3)).toRecipe(
                8009, item = CORK_GREEN_FLAME,
            ),

            (oneOfEach(BOOK, EYE, MASK) + Ingredient(GREEN_EGG, 3)).toRecipe(
                8010, item = CORK_KING_IN_YELLOW,
            ),

            (oneOfEach(BOOK, CLOCK, EYE) + Ingredient(VIOLET_SCROLL, 3)).toRecipe(
                8011, item = CORK_MI_GO,
            ),

            (oneOfEach(RING, SCYTHE, TEAR) + Ingredient(GREEN_SCROLL, 3)).toRecipe(
                8012, item = CORK_NAMELESS_WINDS,
            ),

            (oneOfEach(BLACK_STONE, SCYTHE, TEAR) + Ingredient(BLUE_EGG, 3)).toRecipe(
                8013, item = CORK_NINGISHZIDA,
            ),

            (oneOfEach(BOOK, MASK, TEAR) + Ingredient(ORANGE_EGG, 3)).toRecipe(
                8014, item = CORK_NYARLATHOTEP,
            ),

            (oneOfEach(EYE, PLATE, RING) + Ingredient(GREEN_EGG, 3)).toRecipe(
                8015, item = CORK_PNAKOTIC_HORRORS,
            ),

            (oneOfEach(BOOK, MASK, RING) + Ingredient(VIOLET_SCROLL, 3)).toRecipe(
                8016, item = CORK_RED_MASK,
            ),

            (oneOfEach(BLACK_STONE, CLOCK, TEAR) + Ingredient(GREEN_SCROLL, 3)).toRecipe(
                8017, item = CORK_SHUB_NIGGURATH,
            ),

            (oneOfEach(CLOCK, PLATE, SCYTHE) + Ingredient(BLUE_EGG, 3)).toRecipe(
                8018, item = CORK_TZONTEMOC,
            ),

            (oneOfEach(BOOK, CLOCK, RING) + Ingredient(ORANGE_EGG, 3)).toRecipe(
                8019, item = CORK_YERLEG,
            ),

            (oneOfEach(EYE, RING, TEAR) + Ingredient(GREEN_EGG, 3)).toRecipe(
                8020,
                item = CORK_YOG_SOTHOTH,
            ),
        )
    }

    private fun oneOfEach(vararg items: Item): List<Ingredient> {
        return items.map {
            Ingredient(it, 1)
        }
    }

}