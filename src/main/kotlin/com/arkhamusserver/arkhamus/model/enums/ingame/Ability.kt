package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.*

private const val SECOND_IN_MILLIS: Long = 1 * 1000
private const val MINUTE_IN_MILLIS: Long = SECOND_IN_MILLIS * 60

enum class Ability(
    val id: Int,
    val requiresItem: Boolean = false,
    val consumesItem: Boolean = false,
    val classBased: Boolean = false,
    val availableFor: Set<RoleTypeInGame> = setOf(CULTIST, INVESTIGATOR, NEUTRAL),
    val cooldown: Long? = null,
    val globalCooldown: Boolean = false
) {

    HEAL_MADNESS(
        id = 101,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS
    ),
    THROW_POTATO(
        id = 102,
        requiresItem = true,
        consumesItem = true,
        cooldown = SECOND_IN_MILLIS * 30,
        globalCooldown = true
    ),
    SUMMON_NIGHT(
        id = 201,
        requiresItem = true,
        consumesItem = true,
        availableFor = setOf(CULTIST),
        cooldown = MINUTE_IN_MILLIS * 5,
        globalCooldown = true
    ), ;

    companion object {
        private val abilityMap = values().associateBy { it.id }
        fun byId(abilityId: Int): Ability? {
            return abilityMap[abilityId]
        }
    }
}