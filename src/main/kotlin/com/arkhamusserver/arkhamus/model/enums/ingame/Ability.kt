package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.*

private const val SECOND_IN_MILLIS: Long = 1 * 1000
private const val MINUTE_IN_MILLIS: Long = SECOND_IN_MILLIS * 60
private const val MINIMUM_COOLDOWN: Long = SECOND_IN_MILLIS
private const val DEFAULT_INVESTIGATION_ACTIVE: Long = MINUTE_IN_MILLIS

enum class Ability(
    val id: Int,
    val requiresItem: Boolean = false,
    val consumesItem: Boolean = false,
    val classBased: Boolean = false,
    val availableForRole: Set<RoleTypeInGame> = setOf(CULTIST, INVESTIGATOR, NEUTRAL),
    val cooldown: Long = MINIMUM_COOLDOWN,
    val active: Long? = null,
    val globalCooldown: Boolean = false
) {
    // investigator ability 1***
    HEAL_MADNESS(
        id = 1001,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS
    ),

    //investigator items 2***
    //cultist class items 3***
    //cultist items spell 4***
    THROW_POTATO(
        id = 202,
        requiresItem = true,
        consumesItem = true,
        //available for everybody couse can ber re-thrown
        cooldown = SECOND_IN_MILLIS * 30,
        globalCooldown = true
    ),
    SUMMON_NIGHT(
        id = 202,
        requiresItem = true,
        consumesItem = true,
        availableForRole = setOf(CULTIST),
        cooldown = MINUTE_IN_MILLIS * 5,
        globalCooldown = true
    ),

    //clue items 5***
    SEARCH_FOR_INSCRIPTION(
        id = 5001,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS * 2,
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_SOUND(
        id = 5002,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS * 2,
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_SCENT(
        id = 5003,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS * 2,
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_AURA(
        id = 5004,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS * 2,
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_CORRUPTION(
        id = 5005,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS * 2,
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_OMEN(
        id = 5006,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS * 2,
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_DISTORTION(
        id = 5007,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS * 2,
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    ;

    companion object {
        private val abilityMap = values().associateBy { it.id }
        fun byId(abilityId: Int): Ability? {
            return abilityMap[abilityId]
        }
    }
}