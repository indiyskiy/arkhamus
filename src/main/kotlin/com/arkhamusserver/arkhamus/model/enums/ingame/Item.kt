package com.arkhamusserver.arkhamus.model.enums.ingame

enum class Item(private val itemType: ItemType) {
    BOOK(ItemType.RARE_LOOT),
    CLOCK(ItemType.RARE_LOOT),
    MASK(ItemType.RARE_LOOT),
    RING(ItemType.RARE_LOOT),
    EYE(ItemType.RARE_LOOT),
    TEAR(ItemType.RARE_LOOT),
    SCYTHE(ItemType.RARE_LOOT),

    CORK_YERLEG(ItemType.CORK),
    CORK_CYBELE(ItemType.CORK),
    CORK_BELETH(ItemType.CORK),
    CORK_CTHULHU(ItemType.CORK),
    CORK_KING_IN_YELLOW(ItemType.CORK),
    CORK_TZONTEMOC(ItemType.CORK),
    CORK_BHOLES(ItemType.CORK),
    CORK_AAMON(ItemType.CORK),
    CORK_NINGISHZIDA(ItemType.CORK),
    CORK_YOG_SOTHOTH(ItemType.CORK),
    CORK_MI_GO(ItemType.CORK),
    CORK_NAMELESS_WINDS(ItemType.CORK),
    CORK_COLOUR_OUT_OF_SPACE(ItemType.CORK),
    CORK_DAGON(ItemType.CORK),
    CORK_CZEOTHOQUA(ItemType.CORK),
    CORK_SHUB_NIGGURATH(ItemType.CORK),
    CORK_GREEN_FLAME(ItemType.CORK),
    CORK_RED_MASK(ItemType.CORK),
    CORK_PNAKOTIC_HORRORS(ItemType.CORK),
    CORK_NYARLATHOTEP(ItemType.CORK),
    
    I1(ItemType.LOOT),
    I2(ItemType.LOOT),
    I3(ItemType.LOOT),
    I4(ItemType.LOOT),
    I5(ItemType.LOOT);

    fun getItemType(): ItemType {
        return itemType
    }
}