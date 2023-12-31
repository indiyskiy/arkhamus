package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.GodType.*

enum class God(private val godTypes: List<GodType>) {
    AAMON(
        listOf(INSCRIPTION, CORRUPTION, DISTORTION)
    ),
    BELETH(
        listOf(INSCRIPTION, SOUND, CORRUPTION)
    ),
    BHOLES(
        listOf(INSCRIPTION, SCENT, CORRUPTION)
    ),
    COLOUR_OUT_OF_SPACE(
        listOf(SOUND, AURA, CORRUPTION)
    ),
    CTHULHU(
        listOf(INSCRIPTION, SOUND, OMEN)
    ),
    CYBELE(
        listOf(INSCRIPTION, SOUND, AURA)
    ),
    CZEOTHOQUA(
        listOf(SOUND, SCENT, OMEN)
    ),
    DAGON(
        listOf(SOUND, SCENT, CORRUPTION)
    ),
    GREEN_FLAME(
        listOf(SCENT, AURA, DISTORTION)
    ),
    KING_IN_YELLOW(
        listOf(INSCRIPTION, SCENT, OMEN)
    ),
    MI_GO(
        listOf(SOUND, OMEN, DISTORTION)
    ),
    NAMELESS_WINDS(
        listOf(SOUND, AURA, DISTORTION)
    ),
    NINGISHZIDA(
        listOf(INSCRIPTION, CORRUPTION, OMEN)
    ),
    NYARLATHOTEP(
        listOf(AURA, OMEN, DISTORTION)
    ),
    PNAKOTIC_HORRORS(
        listOf(AURA, CORRUPTION, DISTORTION)
    ),
    RED_MASK(
        listOf(SCENT, AURA, CORRUPTION)
    ),
    SHUB_NIGGURATH(
        listOf(SCENT, AURA, OMEN)
    ),
    TZONTEMOC(
        listOf(INSCRIPTION, SCENT, DISTORTION)
    ),
    YERLEG(
        listOf(INSCRIPTION, SOUND, SCENT)
    ),
    YOG_SOTHOTH(
        listOf(INSCRIPTION, OMEN, DISTORTION)
    );

    fun getTypes(): List<GodType> {
        return godTypes
    }
}