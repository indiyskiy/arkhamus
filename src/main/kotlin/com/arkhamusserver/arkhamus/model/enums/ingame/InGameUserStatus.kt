package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.InGameUserStatusType.MADNESS_DEBUFF
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameUserStatusType.MADNESS_SOURCE

enum class InGameUserStatus(
    val id: Int,
    val type: InGameUserStatusType,
) {
    //MADNESS DEBUFFS 10**
    //lvl 1
    BLIND(1011, MADNESS_DEBUFF),
    PSYCHIC_UNSTABLE(1012, MADNESS_DEBUFF),

    //lvl 2
    CURSED_AURA(1021, MADNESS_DEBUFF),
    MAGIC_ADDICTED(1022, MADNESS_DEBUFF),
    CRAFT_ADDICTED(1023, MADNESS_DEBUFF),
    BAN_ADDICTED(1023, MADNESS_DEBUFF),
    LIGHT_ADDICTED(1023, MADNESS_DEBUFF),

    //lvl 3
    UNSTABLE_POSITION(1031, MADNESS_DEBUFF),
    PROPHET(1032, MADNESS_DEBUFF),

    //MADNESS SOURCE 11**
    NIGHT_MADNESS(1101, MADNESS_SOURCE),
    CURSED_POTATO(1102, MADNESS_SOURCE),
    MADNESS_LINK(1103, MADNESS_SOURCE),
    PEEKABOO_CURSE(1104, MADNESS_SOURCE),
    RITUAL_KICK(1105, MADNESS_SOURCE),
}