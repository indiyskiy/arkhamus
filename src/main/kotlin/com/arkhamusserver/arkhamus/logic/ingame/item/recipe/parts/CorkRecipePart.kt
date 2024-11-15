package com.arkhamusserver.arkhamus.logic.ingame.item.recipe.parts

import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.Recipe
import com.arkhamusserver.arkhamus.logic.ingame.item.recipe.toRecipe
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
import org.springframework.stereotype.Component
import kotlin.collections.plus

@Component
class CorkRecipePart() : RecipeSourcePart {

    companion object {
        const val SIMPLE_LOOT_COUNT = 10
    }

    override fun recipes(): List<Recipe> {
        return listOf(
            (oneOfEach(BOOK, CLOCK, MASK) + Ingredient(SAINT_QUARTZ, SIMPLE_LOOT_COUNT)).toRecipe(
                8001, item = CORK_AAMON,
            ),

            (oneOfEach(EYE, PLATE, SCYTHE) + Ingredient(CRYSTALLIZED_BLOOD, SIMPLE_LOOT_COUNT)).toRecipe(
                8002, item = CORK_BELETH,
            ),

            (oneOfEach(MASK, PLATE, TEAR) + Ingredient(ELDER_SIGN, SIMPLE_LOOT_COUNT)).toRecipe(
                8003, item = CORK_BHOLES,
            ),

            (oneOfEach(CLOCK, EYE, TEAR) + Ingredient(CORRUPTED_TOPAZ, SIMPLE_LOOT_COUNT)).toRecipe(
                8004, item = CORK_COLOUR_OUT_OF_SPACE,
            ),

            (oneOfEach(CLOCK, MASK, PLATE) + Ingredient(HIGGS_BOSON, SIMPLE_LOOT_COUNT)).toRecipe(
                8005, item = CORK_CTHULHU,
            ),

            (oneOfEach(BLACK_STONE, BOOK, PLATE) + Ingredient(RAGS, SIMPLE_LOOT_COUNT)).toRecipe(
                8006, item = CORK_CYBELE,
            ),

            (oneOfEach(BLACK_STONE, PLATE, RING) + Ingredient(BLIGHTING_JEWEL, SIMPLE_LOOT_COUNT)).toRecipe(
                8007, item = CORK_CZEOTHOQUA,
            ),

            (oneOfEach(BLACK_STONE, MASK, SCYTHE) + Ingredient(SAINT_QUARTZ, SIMPLE_LOOT_COUNT)).toRecipe(
                8008, item = CORK_DAGON,
            ),

            (oneOfEach(BLACK_STONE, EYE, SCYTHE) + Ingredient(CRYSTALLIZED_BLOOD, SIMPLE_LOOT_COUNT)).toRecipe(
                8009, item = CORK_GREEN_FLAME,
            ),

            (oneOfEach(BOOK, EYE, MASK) + Ingredient(ELDER_SIGN, SIMPLE_LOOT_COUNT)).toRecipe(
                8010, item = CORK_KING_IN_YELLOW,
            ),

            (oneOfEach(BOOK, CLOCK, EYE) + Ingredient(CORRUPTED_TOPAZ, SIMPLE_LOOT_COUNT)).toRecipe(
                8011, item = CORK_MI_GO,
            ),

            (oneOfEach(RING, SCYTHE, TEAR) + Ingredient(HIGGS_BOSON, SIMPLE_LOOT_COUNT)).toRecipe(
                8012, item = CORK_NAMELESS_WINDS,
            ),

            (oneOfEach(BLACK_STONE, SCYTHE, TEAR) + Ingredient(RAGS, SIMPLE_LOOT_COUNT)).toRecipe(
                8013, item = CORK_NINGISHZIDA,
            ),

            (oneOfEach(BOOK, MASK, TEAR) + Ingredient(BLIGHTING_JEWEL, SIMPLE_LOOT_COUNT)).toRecipe(
                8014, item = CORK_NYARLATHOTEP,
            ),

            (oneOfEach(EYE, PLATE, RING) + Ingredient(SAINT_QUARTZ, SIMPLE_LOOT_COUNT)).toRecipe(
                8015, item = CORK_PNAKOTIC_HORRORS,
            ),

            (oneOfEach(BOOK, MASK, RING) + Ingredient(CRYSTALLIZED_BLOOD, SIMPLE_LOOT_COUNT)).toRecipe(
                8016, item = CORK_RED_MASK,
            ),

            (oneOfEach(BLACK_STONE, CLOCK, TEAR) + Ingredient(ELDER_SIGN, SIMPLE_LOOT_COUNT)).toRecipe(
                8017, item = CORK_SHUB_NIGGURATH,
            ),

            (oneOfEach(CLOCK, PLATE, SCYTHE) + Ingredient(CORRUPTED_TOPAZ, SIMPLE_LOOT_COUNT)).toRecipe(
                8018, item = CORK_TZONTEMOC,
            ),

            (oneOfEach(BOOK, CLOCK, RING) + Ingredient(HIGGS_BOSON, SIMPLE_LOOT_COUNT)).toRecipe(
                8019, item = CORK_YERLEG,
            ),

            (oneOfEach(EYE, RING, TEAR) + Ingredient(RAGS, SIMPLE_LOOT_COUNT)).toRecipe(
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