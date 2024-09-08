package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.DAY_LENGTH_MINUTES
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.NIGHT_LENGTH_MINUTES
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.SECOND_IN_MILLIS
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.*

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
    SPAWN_LOOT(
        id = 1002,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS / 2
    ),

    TOWN_PORTAL_BY_SCROLL(
        id = 1102,
        cooldown = MINUTE_IN_MILLIS * 5,
        requiresItem = true,
        consumesItem = true,
    ),

    //cultist ability 2***
    FARSIGHT(
        id = 2001,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS / 2
    ),
    SEE_THE_OMEN(
        id = 2002,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS * 5
    ),

    //neutral ability 3***
    //cultist items spell 4***
    THROW_POTATO(
        id = 4001,
        requiresItem = true,
        consumesItem = true,
        //available for everybody couse can ber re-thrown
        cooldown = SECOND_IN_MILLIS * 30,
        globalCooldown = true
    ),
    SUMMON_NIGHT(
        id = 4002,
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

    //ADVANCED_USEFUL_ITEM 9***
    KINDLE_CLOAK(
        id = 9001,
        requiresItem = true,
        consumesItem = false,
        cooldown = MINUTE_IN_MILLIS * DAY_LENGTH_MINUTES,
        active = MINUTE_IN_MILLIS * NIGHT_LENGTH_MINUTES / 2
    )
    ;

    companion object {
        private val abilityMap = values().associateBy { it.id }
        fun byId(abilityId: Int): Ability? {
            return abilityMap[abilityId]
        }
    }
}