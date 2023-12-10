package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.GodType.*

enum class God(private val godTypes: List<GodType>) {
    YERLEG(
        listOf(INSCRIPTION, SOUND, SCENT)
    ),
    CYBELE(
        listOf(INSCRIPTION, SOUND, AURA)
    ),
    BELETH(
        listOf(INSCRIPTION, SOUND, CORRUPTION)
    ),
    CTHULHU(
        listOf(INSCRIPTION, SOUND, OMEN)
    ),
    KING_IN_YELLOW(
        listOf(INSCRIPTION, SCENT, OMEN)
    ),
    TZONTEMOC(
        listOf(INSCRIPTION, SCENT, DISTORTION)
    ),
    BHOLES(
        listOf(INSCRIPTION, SCENT, CORRUPTION)
    ),
    AAMON(
        listOf(INSCRIPTION, CORRUPTION, DISTORTION)
    ),
    NINGISHZIDA(
        listOf(INSCRIPTION, CORRUPTION, OMEN)
    ),
    YOG_SOTHOTH(
        listOf(INSCRIPTION, OMEN, DISTORTION)
    ),
    MI_GO(
        listOf(SOUND, OMEN, DISTORTION)
    ),
    NAMELESS_WINDS(
        listOf(SOUND, AURA, DISTORTION)
    ),
    COLOUR_OUT_OF_SPACE(
        listOf(SOUND, AURA, CORRUPTION)
    ),
    DAGON(
        listOf(SOUND, SCENT, CORRUPTION)
    ),
    CZEOTHOQUA(
        listOf(SOUND, SCENT, OMEN)
    ),
    SHUB_NIGGURATH(
        listOf(SCENT, AURA, OMEN)
    ),
    GREEN_FLAME(
        listOf(SCENT, AURA, DISTORTION)
    ),
    RED_MASK(
        listOf(SCENT, AURA, CORRUPTION)
    ),
    PNAKOTIC_HORRORS(
        listOf(AURA, CORRUPTION, DISTORTION)
    ),
    NYARLATHOTEP(
        listOf(AURA, OMEN, DISTORTION)
    );

    fun getTypes(): List<GodType> {
        return godTypes
    }
}