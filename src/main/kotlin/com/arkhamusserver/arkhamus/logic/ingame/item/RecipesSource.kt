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
        private const val SECOND_IN_MILLIS: Long = 1 * 1000
        private const val MINUTE_IN_MILLIS: Long = SECOND_IN_MILLIS * 60
    }

    fun getAllRecipes() = allRecipes
    fun byId(id: Int) = allRecipesMap[id]

    private fun buildAll(): List<Recipe> {
        return listOf(
//INVESTIGATION ITEM 5***
            listOf(
                Ingredient(SCYTHE, 1),
//                Ingredient(BLIGHTING_JEWEL, 5),
                Ingredient(INNSMOUTH_WATER, 5)
            ).toRecipe(
                5001,
                item = INSCRIPTION_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(BLACK_STONE, 1),
//                Ingredient(INNSMOUTH_WATER, 5),
                Ingredient(HIGGS_BOSON, 5)
            ).toRecipe(
                5002,
                item = SOUND_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(RING, 1),
//                Ingredient(CLOUDY_FLASK, 5),
                Ingredient(CORRUPTED_TOPAZ, 5)
            ).toRecipe(
                5003,
                item = SCENT_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(TEAR, 1),
//                Ingredient(CORRUPTED_TOPAZ, 5),
                Ingredient(ELDER_SIGN, 5)
            ).toRecipe(
                5004,
                item = AURA_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(PLATE, 1),
//                Ingredient(ELDER_SIGN, 5),
                Ingredient(CRYSTALLIZED_BLOOD, 5)
            ).toRecipe(
                5005,
                item = CORRUPTION_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(EYE, 1),
//                Ingredient(CRYSTALLIZED_BLOOD, 5),
                Ingredient(SAINT_QUARTZ, 5)
            ).toRecipe(
                5006,
                item = OMEN_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
            listOf(
                Ingredient(MASK, 1),
//                Ingredient(SAINT_QUARTZ, 5),
                Ingredient(BLIGHTING_JEWEL, 5)
            ).toRecipe(
                5007,
                item = DISTORTION_INVESTIGATION_ITEM,
                timeToCraft = MINUTE_IN_MILLIS / 2,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED)
            ),
//USEFUL_ITEM 6***
            listOf(
                Ingredient(ELDER_SIGN, 1),
                Ingredient(HIGGS_BOSON, 1)
            ).toRecipe(
                6001,
                item = VOTE_TOKEN,
                timeToCraft = SECOND_IN_MILLIS * 3,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),
// CULTIST ITEMS 7***
            listOf(Ingredient(SAINT_QUARTZ, 1), Ingredient(DARK_ESSENCE, 1)).toRecipe(
                7001,
                item = MOON_STONE,
                timeToCraft = MINUTE_IN_MILLIS,
                crafterTypes = listOf(CrafterType.REGULAR, CrafterType.ADVANCED, CrafterType.CULTIST)
            ),

            listOf(Ingredient(CRYSTALLIZED_BLOOD, 2), Ingredient(STRANGE_BONE, 2)).toRecipe(
                7002,
                item = CURSED_POTATO,
                timeToCraft = (0.5 * MINUTE_IN_MILLIS).roundToLong(),
                numberOfItems = 3,
                crafterTypes = listOf(CrafterType.ADVANCED, CrafterType.CULTIST)
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

