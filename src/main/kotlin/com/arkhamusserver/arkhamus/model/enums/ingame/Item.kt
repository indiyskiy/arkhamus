package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType.*

enum class Item(private val itemType: ItemType) {
    BLACK_STONE(RARE_LOOT),
    BOOK(RARE_LOOT),
    CLOCK(RARE_LOOT),
    EYE(RARE_LOOT),
    MASK(RARE_LOOT),
    PLATE(RARE_LOOT),
    RING(RARE_LOOT),
    SCYTHE(RARE_LOOT),
    TEAR(RARE_LOOT),

    CORK_AAMON(CORK),
    CORK_BELETH(CORK),
    CORK_BHOLES(CORK),
    CORK_COLOUR_OUT_OF_SPACE(CORK),
    CORK_CTHULHU(CORK),
    CORK_CYBELE(CORK),
    CORK_CZEOTHOQUA(CORK),
    CORK_DAGON(CORK),
    CORK_GREEN_FLAME(CORK),
    CORK_KING_IN_YELLOW(CORK),
    CORK_MI_GO(CORK),
    CORK_NAMELESS_WINDS(CORK),
    CORK_NINGISHZIDA(CORK),
    CORK_NYARLATHOTEP(CORK),
    CORK_PNAKOTIC_HORRORS(CORK),
    CORK_RED_MASK(CORK),
    CORK_SHUB_NIGGURATH(CORK),
    CORK_TZONTEMOC(CORK),
    CORK_YERLEG(CORK),
    CORK_YOG_SOTHOTH(CORK),

    I1(LOOT),
    I2(LOOT),
    I3(LOOT),
    I4(LOOT),
    I5(LOOT);

    fun getItemType(): ItemType {
        return itemType
    }
}