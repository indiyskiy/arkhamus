package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.Item.*
import org.springframework.stereotype.Component

@Component
class ItemToRecipeResolver {
    fun resolve(item: Item): Recipe =
        when (item) {
            CORK_YERLEG -> listOf(
                ingridient(BOOK, 1),
                ingridient(CLOCK, 3)
            ).toRecipe()

            CORK_CYBELE -> listOf(
                ingridient(EYE, 2),
                ingridient(TEAR, 2)
            ).toRecipe()

            CORK_BELETH -> listOf(
                ingridient(CLOCK, 1), //YERLEG
                ingridient(MASK, 3)
            ).toRecipe()

            CORK_CTHULHU -> listOf(
                ingridient(RING, 3),
                ingridient(SCYTHE, 1)
            ).toRecipe()

            CORK_KING_IN_YELLOW -> listOf(
                ingridient(CLOCK, 1), //YERLEG
                ingridient(EYE, 3)
            ).toRecipe()

            CORK_TZONTEMOC -> listOf(
                ingridient(TEAR, 3),
                ingridient(SCYTHE, 1)
            ).toRecipe()

            CORK_BHOLES -> listOf(
                ingridient(MASK, 1), //BELETH
                ingridient(RING, 3)
            ).toRecipe()

            CORK_AAMON -> listOf(
                ingridient(BOOK, 1),
                ingridient(SCYTHE, 3) //TZONTEMOC
            ).toRecipe()

            CORK_NINGISHZIDA -> listOf(
                ingridient(CLOCK, 2), //BELETH, KING_IN_YELLOW
                ingridient(RING, 2) //BHOLES
            ).toRecipe()

            CORK_YOG_SOTHOTH -> listOf(
                ingridient(BOOK, 3), //AAMON
                ingridient(MASK, 1)
            ).toRecipe()

            CORK_MI_GO -> listOf(
                ingridient(BOOK, 1), //YOG_SOTHOTH
                ingridient(EYE, 3)
            ).toRecipe()

            CORK_NAMELESS_WINDS -> listOf(
                ingridient(CLOCK, 3),
                ingridient(SCYTHE, 1)
            ).toRecipe()

            CORK_COLOUR_OUT_OF_SPACE -> listOf(
                ingridient(BOOK, 3),
                ingridient(RING, 1)
            ).toRecipe()

            CORK_DAGON -> listOf(
                ingridient(EYE, 2),
                ingridient(SCYTHE, 2)
            ).toRecipe()

            CORK_CZEOTHOQUA -> listOf(
                ingridient(MASK, 3),
                ingridient(TEAR, 1)
            ).toRecipe()

            CORK_SHUB_NIGGURATH -> listOf(
                ingridient(MASK, 1), //CZEOTHOQUA
                ingridient(SCYTHE, 3)
            ).toRecipe()

            CORK_GREEN_FLAME -> listOf(
                ingridient(MASK, 3), //SHUB_NIGGURATH
                ingridient(EYE, 1)
            ).toRecipe()

            CORK_RED_MASK -> listOf(
                ingridient(BOOK, 1), //COLOUR_OUT_OF_SPACE
                ingridient(TEAR, 3)
            ).toRecipe()

            CORK_PNAKOTIC_HORRORS -> listOf(
                ingridient(CLOCK, 2), //NAMELESS_WINDS
                ingridient(TEAR, 2) //RED_MASK
            ).toRecipe()

            CORK_NYARLATHOTEP -> listOf(
                ingridient(RING, 3),
                ingridient(TEAR, 1) //PNAKOTIC_HORRORS
            ).toRecipe()

            else -> emptyRecipe()
        }
}

