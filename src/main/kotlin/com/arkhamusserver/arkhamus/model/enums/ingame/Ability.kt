package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.*

enum class Ability(
    val id: Int,
    val requiresItem: Boolean = false,
    val consumesItem: Boolean = false,
    val classBased: Boolean = false,
    val availableFor: Set<RoleTypeInGame> = setOf(CULTIST, INVESTIGATOR, NEUTRAL),
) {
    HEAL_MADNESS(
        id = 101,
        classBased = true,
    ),
    SUMMON_NIGHT(
        id = 201,
        requiresItem = true,
        consumesItem = true,
        availableFor = setOf(CULTIST),
    ), ;

    companion object {
        private val abilityMap = values().associateBy { it.id }
        fun byId(abilityId: Int): Ability? {
            return abilityMap[abilityId]
        }
    }
}