package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.Item.*
import org.springframework.stereotype.Component

@Component
class GodCorkToReceiptResolver {
    fun resolve(item: Item): List<Item> =
        when (item) {
            CORK_YERLEG -> listOf(BOOK, CLOCK)
            CORK_CYBELE -> listOf(EYE, TEAR)
            CORK_BELETH -> listOf(CLOCK, MASK)
            CORK_CTHULHU -> listOf(RING, SCYTHE)
            CORK_KING_IN_YELLOW -> listOf(CLOCK, EYE)
            CORK_TZONTEMOC -> listOf(TEAR, SCYTHE)
            CORK_BHOLES -> listOf(MASK, RING)
            CORK_AAMON -> listOf(BOOK, SCYTHE)
            CORK_NINGISHZIDA -> listOf(CLOCK, RING)
            CORK_YOG_SOTHOTH -> listOf(BOOK, MASK)
            CORK_MI_GO -> listOf(BOOK, EYE)
            CORK_NAMELESS_WINDS -> listOf(CLOCK, SCYTHE)
            CORK_COLOUR_OUT_OF_SPACE -> listOf(BOOK, RING)
            CORK_DAGON -> listOf(EYE, SCYTHE)
            CORK_CZEOTHOQUA -> listOf(MASK, TEAR)
            CORK_SHUB_NIGGURATH -> listOf(MASK, SCYTHE)
            CORK_GREEN_FLAME -> listOf(MASK, EYE)
            CORK_RED_MASK -> listOf(BOOK, TEAR)
            CORK_PNAKOTIC_HORRORS -> listOf(CLOCK, TEAR)
            CORK_NYARLATHOTEP -> listOf(RING, TEAR)
            else -> emptyList()
        }
}