package com.arkhamusserver.arkhamus.model.enums.ingame

enum class MadnessDebuffs(private val stepNumber: Int) {
    //0
    BLIND(0),  //implemented
//    SLOW(0),
    PSYCHIC_UNSTABLE(0), //implemented

    //1
    CURSED_AURA(1),
    MAGIC_ADDICTED(1),
    CRAFT_ADDICTED(1),
    BAN_ADDICTED(1),
    LIGHT_ADDICTED(1),

    //2
    UNSTABLE_POSITION(2),
    DARK_ENTITY(2),
    PROPHET(2);

    fun getStepNumber(): Int {
        return stepNumber
    }
}