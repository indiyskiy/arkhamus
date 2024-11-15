package com.arkhamusserver.arkhamus.model.enums.ingame.core

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue.*
import com.arkhamusserver.arkhamus.model.enums.ingame.core.God.values

enum class God(
    private val id: Int,
    private val clues: List<Clue>
) {
    AAMON(
        1,
        listOf(INSCRIPTION, CORRUPTION, DISTORTION)
    ),
    BELETH(
        2,
        listOf(INSCRIPTION, SOUND, CORRUPTION)
    ),
    BHOLES(
        3,
        listOf(INSCRIPTION, SCENT, CORRUPTION)
    ),
    COLOUR_OUT_OF_SPACE(
        4,
        listOf(SOUND, AURA, CORRUPTION)
    ),
    CTHULHU(
        5,
        listOf(INSCRIPTION, SOUND, OMEN)
    ),
    CYBELE(
        6,
        listOf(INSCRIPTION, SOUND, AURA)
    ),
    CZEOTHOQUA(
        7,
        listOf(SOUND, SCENT, OMEN)
    ),
    DAGON(
        8,
        listOf(SOUND, SCENT, CORRUPTION)
    ),
    GREEN_FLAME(
        9,
        listOf(SCENT, AURA, DISTORTION)
    ),
    KING_IN_YELLOW(
        10,
        listOf(INSCRIPTION, SCENT, OMEN)
    ),
    MI_GO(
        11,
        listOf(SOUND, OMEN, DISTORTION)
    ),
    NAMELESS_WINDS(
        12,
        listOf(SOUND, AURA, DISTORTION)
    ),
    NINGISHZIDA(
        13,
        listOf(INSCRIPTION, CORRUPTION, OMEN)
    ),
    NYARLATHOTEP(
        14,
        listOf(AURA, OMEN, DISTORTION)
    ),
    PNAKOTIC_HORRORS(
        15,
        listOf(AURA, CORRUPTION, DISTORTION)
    ),
    RED_MASK(
        16,
        listOf(SCENT, AURA, CORRUPTION)
    ),
    SHUB_NIGGURATH(
        17,
        listOf(SCENT, AURA, OMEN)
    ),
    TZONTEMOC(
        18,
        listOf(INSCRIPTION, SCENT, DISTORTION)
    ),
    YERLEG(
        19,
        listOf(INSCRIPTION, SOUND, SCENT)
    ),
    YOG_SOTHOTH(
        20,
        listOf(INSCRIPTION, OMEN, DISTORTION)
    );

    fun getTypes(): List<Clue> =
        clues


    fun getId(): Int =
        id

    companion object {
        private val godsMap: Map<Int, God> = values().associateBy { it.id }
        fun getGodsMap(): Map<Int, God> = godsMap
    }
}

fun Int.toGod() = God.getGodsMap()[this]