package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.Item.*
import org.springframework.stereotype.Component
import kotlin.math.roundToLong

@Component
class RecipesSource {

    private val allRecipes = buildAll()
    private val allRecipesMap = allRecipes.associateBy { it.recipeId }

    companion object {
        private const val SECOND_IN_MILIS: Long = 1 * 1000
        private const val MINUTE_IN_MILIS: Long = SECOND_IN_MILIS * 60
    }

    fun getAllRecipes() = allRecipes
    fun byId(id: Int) = allRecipesMap[id]

    private fun buildAll(): List<Recipe> {
        return listOf(
// CULTIST ITEMS 7***
            listOf(Ingredient(SAINT_QUARTZ, 1), Ingredient(DARK_ESSENCE, 1)).toRecipe(
                    7001,
                    item = MOON_STONE,
                    timeToCraft = MINUTE_IN_MILIS,
                    numberOfItems = 1,
                    crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
                ),

            listOf(Ingredient(SAINT_QUARTZ, 2), Ingredient(DARK_ESSENCE, 2)).toRecipe(
                    7002,
                    item = MOON_STONE,
                    timeToCraft = (0.5 * MINUTE_IN_MILIS).roundToLong(),
                    numberOfItems = 3,
                    crafterTypes = listOf(CrafterType.ADVANCED)
                ),
// CORKS 8***
            (oneOfEach(BOOK, CLOCK, MASK) + Ingredient(VIOLET_SCROLL, 10)).toRecipe(
                    8001, item = CORK_AAMON,
                ),

            (oneOfEach(EYE, PLATE, SCYTHE) + Ingredient(GREEN_SCROLL, 10)).toRecipe(
                    8002, item = CORK_BELETH,
                ),

            (oneOfEach(MASK, PLATE, TEAR) + Ingredient(BLUE_EGG, 10)).toRecipe(
                    8003, item = CORK_BHOLES,
                ),

            (oneOfEach(CLOCK, EYE, TEAR) + Ingredient(ORANGE_EGG, 10)).toRecipe(
                    8004, item = CORK_COLOUR_OUT_OF_SPACE,
                ),

            (oneOfEach(CLOCK, MASK, PLATE) + Ingredient(GREEN_EGG, 10)).toRecipe(
                    8005, item = CORK_CTHULHU,
                ),

            (oneOfEach(BLACK_STONE, BOOK, PLATE) + Ingredient(VIOLET_SCROLL, 10)).toRecipe(
                    8006, item = CORK_CYBELE,
                ),

            (oneOfEach(BLACK_STONE, PLATE, RING) + Ingredient(GREEN_SCROLL, 10)).toRecipe(
                    8007, item = CORK_CZEOTHOQUA,
                ),

            (oneOfEach(BLACK_STONE, MASK, SCYTHE) + Ingredient(BLUE_EGG, 10)).toRecipe(
                    8008, item = CORK_DAGON,
                ),

            (oneOfEach(BLACK_STONE, EYE, SCYTHE) + Ingredient(ORANGE_EGG, 10)).toRecipe(
                    8009, item = CORK_GREEN_FLAME,
                ),

            (oneOfEach(BOOK, EYE, MASK) + Ingredient(GREEN_EGG, 10)).toRecipe(
                    8010, item = CORK_KING_IN_YELLOW,
                ),

            (oneOfEach(BOOK, CLOCK, EYE) + Ingredient(VIOLET_SCROLL, 10)).toRecipe(
                    8011, item = CORK_MI_GO,
                ),

            (oneOfEach(RING, SCYTHE, TEAR) + Ingredient(GREEN_SCROLL, 10)).toRecipe(
                    8012, item = CORK_NAMELESS_WINDS,
                ),

            (oneOfEach(BLACK_STONE, SCYTHE, TEAR) + Ingredient(BLUE_EGG, 10)).toRecipe(
                    8013, item = CORK_NINGISHZIDA,
                ),

            (oneOfEach(BOOK, MASK, TEAR) + Ingredient(ORANGE_EGG, 10)).toRecipe(
                    8014, item = CORK_NYARLATHOTEP,
                ),

            (oneOfEach(EYE, PLATE, RING) + Ingredient(GREEN_EGG, 10)).toRecipe(
                    8015, item = CORK_PNAKOTIC_HORRORS,
                ),

            (oneOfEach(BOOK, MASK, RING) + Ingredient(VIOLET_SCROLL, 10)).toRecipe(
                    8016, item = CORK_RED_MASK,
                ),

            (oneOfEach(BLACK_STONE, CLOCK, TEAR) + Ingredient(GREEN_SCROLL, 10)).toRecipe(
                    8017, item = CORK_SHUB_NIGGURATH,
                ),

            (oneOfEach(CLOCK, PLATE, SCYTHE) + Ingredient(BLUE_EGG, 10)).toRecipe(
                    8018, item = CORK_TZONTEMOC,
                ),

            (oneOfEach(BOOK, CLOCK, RING) + Ingredient(ORANGE_EGG, 10)).toRecipe(
                    8019, item = CORK_YERLEG,
                ),

            (oneOfEach(EYE, RING, TEAR) + Ingredient(GREEN_EGG, 10)).toRecipe(
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

