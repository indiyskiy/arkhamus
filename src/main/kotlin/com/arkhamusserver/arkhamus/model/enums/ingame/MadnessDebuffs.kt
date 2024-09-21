package com.arkhamusserver.arkhamus.model.enums.ingame

enum class MadnessDebuffs(private val stepNumber: Int) {
    //0
    BLIND(0),
    SLOW(0),

    //1
    CURSED_AURA(1),
    MAGIC_ADDICTED(1),

    //2
    DARK_ENTITY(2),
    PROPHET(2);

    fun getStepNumber(): Int {
        return stepNumber
    }
}