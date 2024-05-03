package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.Item.*
import org.springframework.stereotype.Component
import kotlin.math.roundToLong

@Component
class ItemToRecipesSource {

    companion object {
        const val SECOND_IN_MILIS: Long = 1 * 1000
        const val MINUTE_IN_MILIS: Long = SECOND_IN_MILIS * 60
    }

    fun all(): List<Recipe> {
        return listOf(
// CULTIST ITEMS
            listOf(Ingredient(SAINT_QUARTZ, 1), Ingredient(DARK_ESSENCE, 1))
                .toRecipe(
                    item = MOON_STONE,
                    timeToCraft = SECOND_IN_MILIS,
                    numberOfItems = 1,
                    crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
                ),

            listOf(Ingredient(SAINT_QUARTZ, 2), Ingredient(DARK_ESSENCE, 2))
                .toRecipe(
                    item = MOON_STONE,
                    timeToCraft = (1.5 * SECOND_IN_MILIS).roundToLong(),
                    numberOfItems = 3,
                    crafterTypes = listOf(CrafterType.ADVANCED)
                ),
// CORKS
            (oneOfEach(BOOK, CLOCK, MASK) + Ingredient(SAINT_QUARTZ, 10))
                .toRecipe(item = CORK_AAMON),

            (oneOfEach(EYE, PLATE, SCYTHE) + Ingredient(SAINT_QUARTZ, 10))
                .toRecipe(item = CORK_BELETH),

            (oneOfEach(MASK, PLATE, TEAR) + Ingredient(SAINT_QUARTZ, 10))
                .toRecipe(item = CORK_BHOLES),

            (oneOfEach(CLOCK, EYE, TEAR) + Ingredient(SAINT_QUARTZ, 10))
                .toRecipe(item = CORK_COLOUR_OUT_OF_SPACE),

            (oneOfEach(CLOCK, MASK, PLATE) + Ingredient(I2, 10))
                .toRecipe(item = CORK_CTHULHU),

            (oneOfEach(BLACK_STONE, BOOK, PLATE) + Ingredient(I7, 10))
                .toRecipe(item = CORK_CYBELE),

            (oneOfEach(BLACK_STONE, PLATE, RING) + Ingredient(I6, 10))
                .toRecipe(item = CORK_CZEOTHOQUA),

            (oneOfEach(BLACK_STONE, MASK, SCYTHE) + Ingredient(I7, 10))
                .toRecipe(item = CORK_DAGON),

            (oneOfEach(BLACK_STONE, EYE, SCYTHE) + Ingredient(I6, 10))
                .toRecipe(item = CORK_GREEN_FLAME),

            (oneOfEach(BOOK, EYE, MASK) + Ingredient(I5, 10))
                .toRecipe(item = CORK_KING_IN_YELLOW),

            (oneOfEach(BOOK, CLOCK, EYE) + Ingredient(I3, 10))
                .toRecipe(item = CORK_MI_GO),

            (oneOfEach(RING, SCYTHE, TEAR) + Ingredient(I4, 10))
                .toRecipe(item = CORK_NAMELESS_WINDS),

            (oneOfEach(BLACK_STONE, SCYTHE, TEAR) + Ingredient(I5, 10))
                .toRecipe(item = CORK_NINGISHZIDA),

            (oneOfEach(BOOK, MASK, TEAR) + Ingredient(I4, 10))
                .toRecipe(item = CORK_NYARLATHOTEP),

            (oneOfEach(EYE, PLATE, RING) + Ingredient(I2, 10))
                .toRecipe(item = CORK_PNAKOTIC_HORRORS),

            (oneOfEach(BOOK, MASK, RING) + Ingredient(I6, 10))
                .toRecipe(item = CORK_RED_MASK),

            (oneOfEach(BLACK_STONE, CLOCK, TEAR) + Ingredient(I2, 10))
                .toRecipe(item = CORK_SHUB_NIGGURATH),

            (oneOfEach(CLOCK, PLATE, SCYTHE) + Ingredient(I3, 10))
                .toRecipe(item = CORK_TZONTEMOC),

            (oneOfEach(BOOK, CLOCK, RING) + Ingredient(I5, 10))
                .toRecipe(item = CORK_YERLEG),

            (oneOfEach(EYE, RING, TEAR) + Ingredient(I4, 10))
                .toRecipe(item = CORK_YOG_SOTHOTH),
        )
    }

    private fun oneOfEach(vararg items: Item): List<Ingredient> {
        return items.map {
            Ingredient(it, 1)
        }
    }
}

