package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.InGameUserStatusType.MADNESS_DEBUFF

enum class InGameUserStatus(
    val id: Int,
    val type: InGameUserStatusType,
) {
    //1***
    BLIND(1001, MADNESS_DEBUFF),
    PSYCHIC_UNSTABLE(1002, MADNESS_DEBUFF),
    CURSED_AURA(1003, MADNESS_DEBUFF),
    MAGIC_ADDICTED(1004, MADNESS_DEBUFF),
    CRAFT_ADDICTED(1005, MADNESS_DEBUFF),
    BAN_ADDICTED(1006, MADNESS_DEBUFF),
    LIGHT_ADDICTED(1007, MADNESS_DEBUFF),
    UNSTABLE_POSITION(1008, MADNESS_DEBUFF),
    PROPHET(1009, MADNESS_DEBUFF);
}