package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.ItemType.*

enum class Item(
    private val id: Long,
    private val itemType: ItemType
) {
    BLACK_STONE(101, RARE_LOOT),
    BOOK(102, RARE_LOOT),
    CLOCK(103, RARE_LOOT),
    EYE(104, RARE_LOOT),
    MASK(105, RARE_LOOT),
    PLATE(106, RARE_LOOT),
    RING(107, RARE_LOOT),
    SCYTHE(108, RARE_LOOT),
    TEAR(109, RARE_LOOT),

    CORK_AAMON(201, CORK),
    CORK_BELETH(202, CORK),
    CORK_BHOLES(203, CORK),
    CORK_COLOUR_OUT_OF_SPACE(204, CORK),
    CORK_CTHULHU(205, CORK),
    CORK_CYBELE(206, CORK),
    CORK_CZEOTHOQUA(207, CORK),
    CORK_DAGON(208, CORK),
    CORK_GREEN_FLAME(209, CORK),
    CORK_KING_IN_YELLOW(210, CORK),
    CORK_MI_GO(211, CORK),
    CORK_NAMELESS_WINDS(212, CORK),
    CORK_NINGISHZIDA(213, CORK),
    CORK_NYARLATHOTEP(214, CORK),
    CORK_PNAKOTIC_HORRORS(215, CORK),
    CORK_RED_MASK(216, CORK),
    CORK_SHUB_NIGGURATH(217, CORK),
    CORK_TZONTEMOC(218, CORK),
    CORK_YERLEG(219, CORK),
    CORK_YOG_SOTHOTH(220, CORK),

    I1(301, LOOT),
    I2(302, LOOT),
    I3(303, LOOT),
    I4(304, LOOT),
    I5(305, LOOT);

    fun getItemType(): ItemType =
        itemType

    fun getId(): Long =
        id
}