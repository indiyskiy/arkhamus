package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.God.*
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.Item.*
import org.springframework.stereotype.Component

@Component
class GodToCorkResolver {
    fun resolve(god: God): Item =
        when (god) {
            YERLEG -> CORK_YERLEG
            CYBELE -> CORK_CYBELE
            BELETH -> CORK_BELETH
            CTHULHU -> CORK_CTHULHU
            KING_IN_YELLOW -> CORK_KING_IN_YELLOW
            TZONTEMOC -> CORK_TZONTEMOC
            BHOLES -> CORK_BHOLES
            AAMON -> CORK_AAMON
            NINGISHZIDA -> CORK_NINGISHZIDA
            YOG_SOTHOTH -> CORK_YOG_SOTHOTH
            MI_GO -> CORK_MI_GO
            NAMELESS_WINDS -> CORK_NAMELESS_WINDS
            COLOUR_OUT_OF_SPACE -> CORK_COLOUR_OUT_OF_SPACE
            DAGON -> CORK_DAGON
            CZEOTHOQUA -> CORK_CZEOTHOQUA
            SHUB_NIGGURATH -> CORK_SHUB_NIGGURATH
            GREEN_FLAME -> CORK_GREEN_FLAME
            RED_MASK -> CORK_RED_MASK
            PNAKOTIC_HORRORS -> CORK_PNAKOTIC_HORRORS
            NYARLATHOTEP -> CORK_NYARLATHOTEP
        }
}

