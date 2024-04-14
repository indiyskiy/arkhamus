package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame.*
import com.arkhamusserver.arkhamus.model.enums.ingame.God
import com.arkhamusserver.arkhamus.model.enums.ingame.God.*
import org.springframework.stereotype.Component

@Component
class CultistClassByGodResolver {
    fun resolve(god: God): ClassInGame {
        return when (god) {
            AAMON -> AAMON_CULTIST
            BELETH -> BELETH_CULTIST
            BHOLES -> BHOLES_CULTIST
            COLOUR_OUT_OF_SPACE -> COLOUR_OUT_OF_SPACE_CULTIST
            CTHULHU -> CTHULHU_CULTIST
            CYBELE -> CYBELE_CULTIST
            CZEOTHOQUA -> CZEOTHOQUA_CULTIST
            DAGON -> DAGON_CULTIST
            GREEN_FLAME -> GREEN_FLAME_CULTIST
            KING_IN_YELLOW -> KING_IN_YELLOW_CULTIST
            MI_GO -> MI_GO_CULTIST
            NAMELESS_WINDS -> NAMELESS_WINDS_CULTIST
            NINGISHZIDA -> NINGISHZIDA_CULTIST
            NYARLATHOTEP -> NYARLATHOTEP_CULTIST
            PNAKOTIC_HORRORS -> PNAKOTIC_HORRORS_CULTIST
            RED_MASK -> RED_MASK_CULTIST
            SHUB_NIGGURATH -> SHUB_NIGGURATH_CULTIST
            TZONTEMOC -> TZONTEMOC_CULTIST
            YERLEG -> YERLEG_CULTIST
            YOG_SOTHOTH -> YOG_SOTHOTH_CULTIST
        }
    }
}