package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.Item.*
import org.springframework.stereotype.Component

@Component
class ItemToRecipeResolver {

    fun resolve(item: Item): Recipe =
        when (item) {
            CORK_AAMON ->
                oneOfEach(BOOK, CLOCK, MASK)
                    .toRecipe()

            CORK_BELETH ->
                oneOfEach(EYE, PLATE, SCYTHE)
                    .toRecipe()

            CORK_BHOLES ->
                oneOfEach(MASK, PLATE, TEAR)
                    .toRecipe()

            CORK_COLOUR_OUT_OF_SPACE ->
                oneOfEach(CLOCK, EYE, TEAR)
                    .toRecipe()

            CORK_CTHULHU ->
                oneOfEach(CLOCK, MASK, PLATE)
                    .toRecipe()

            CORK_CYBELE ->
                oneOfEach(BLACK_STONE, BOOK, PLATE)
                    .toRecipe()

            CORK_CZEOTHOQUA ->
                oneOfEach(BLACK_STONE, PLATE, RING)
                    .toRecipe()

            CORK_DAGON ->
                oneOfEach(BLACK_STONE, MASK, SCYTHE)
                    .toRecipe()

            CORK_GREEN_FLAME ->
                oneOfEach(BLACK_STONE, EYE, SCYTHE)
                    .toRecipe()

            CORK_KING_IN_YELLOW ->
                oneOfEach(BOOK, EYE, MASK)
                    .toRecipe()

            CORK_MI_GO ->
                oneOfEach(BOOK, CLOCK, EYE)
                    .toRecipe()

            CORK_NAMELESS_WINDS ->
                oneOfEach(RING, SCYTHE, TEAR)
                    .toRecipe()

            CORK_NINGISHZIDA ->
                oneOfEach(BLACK_STONE, SCYTHE, TEAR)
                    .toRecipe()

            CORK_NYARLATHOTEP ->
                oneOfEach(BOOK, MASK, TEAR)
                    .toRecipe()

            CORK_PNAKOTIC_HORRORS ->
                oneOfEach(EYE, PLATE, RING)
                    .toRecipe()

            CORK_RED_MASK ->
                oneOfEach(BOOK, MASK, RING)
                    .toRecipe()

            CORK_SHUB_NIGGURATH ->
                oneOfEach(BLACK_STONE, CLOCK, TEAR)
                    .toRecipe()

            CORK_TZONTEMOC ->
                oneOfEach(CLOCK, PLATE, SCYTHE)
                    .toRecipe()

            CORK_YERLEG ->
                oneOfEach(BOOK, CLOCK, RING)
                    .toRecipe()

            CORK_YOG_SOTHOTH ->
                oneOfEach(EYE, RING, TEAR)
                    .toRecipe()

            else -> emptyRecipe()
        }

    private fun oneOfEach(vararg items: Item): List<Ingredient> {
        return items.map {
            Ingredient(it, 1)
        }
    }
}

