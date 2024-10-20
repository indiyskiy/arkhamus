package com.arkhamusserver.arkhamus.model.enums.ingame.core

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.DAY_LENGTH_MINUTES
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.NIGHT_LENGTH_MINUTES
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.SECOND_IN_MILLIS
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType.*
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame.*
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

private const val MINIMUM_COOLDOWN: Long = SECOND_IN_MILLIS
private const val DEFAULT_INVESTIGATION_ACTIVE: Long = MINUTE_IN_MILLIS

private const val CLOSE_RANGE = 4.0
private const val MEDIUM_RANGE = 8.0
private const val LARGE_RANGE = 16.0

enum class Ability(
    val id: Int,
    val requiresItem: Boolean = false,
    val consumesItem: Boolean = false,
    val classBased: Boolean = false,
    val availableForRole: Set<RoleTypeInGame> = setOf(CULTIST, INVESTIGATOR, NEUTRAL),
    val cooldown: Long = MINIMUM_COOLDOWN,
    val active: Long? = null,
    val globalCooldown: Boolean = false,
    val targetTypes: List<GameObjectType>? = null,
    val range: Double? = null,
    val visibilityModifiers: Set<VisibilityModifier> = setOf(VisibilityModifier.ALL),
    val visibilityModifiersString: MutableSet<String> = visibilityModifiers.map { it.name }.toMutableSet(),
) : WithVisibilityModifiers {
    // investigator ability 1***
    HEAL_MADNESS(
        id = 1001,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS,
        targetTypes = listOf(CHARACTER),
        range = MEDIUM_RANGE
    ),
    SPAWN_LOOT(
        id = 1002,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS / 2
    ),

    //cultist ability 2***
    FARSIGHT(
        id = 2001,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS
    ),
    PARALYSE(
        id = 2002,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS,
        targetTypes = listOf(CHARACTER),
        range = MEDIUM_RANGE
    ),

    //neutral ability 3***
    //useful item ability
    TOWN_PORTAL_BY_SCROLL(
        id = 4002,
        cooldown = MINUTE_IN_MILLIS * 6,
        requiresItem = true,
        consumesItem = true,
    ),
    HEAL_MADNESS_BY_PILL(
        id = 4003,
        cooldown = MINUTE_IN_MILLIS,
        requiresItem = true,
        consumesItem = true,
    ),

    //cultist items spell 5***
    THROW_POTATO(
        id = 5001,
        requiresItem = true,
        consumesItem = true,
        //available for everybody couse can ber re-thrown
        cooldown = SECOND_IN_MILLIS * 30,
        globalCooldown = true,
        targetTypes = listOf(CHARACTER),
        range = LARGE_RANGE
    ),
    SUMMON_NIGHT(
        id = 5002,
        requiresItem = true,
        consumesItem = true,
        availableForRole = setOf(CULTIST),
        cooldown = MINUTE_IN_MILLIS * 5,
        globalCooldown = true
    ),
    PEEKABOO_CURSE_ITEM(
        id = 5003,
        requiresItem = true,
        consumesItem = false,
        availableForRole = setOf(CULTIST),
        cooldown = MINUTE_IN_MILLIS * 1,
        globalCooldown = false,
        targetTypes = listOf(CONTAINER, CRAFTER),
        range = CLOSE_RANGE
    ),
    FAKE_VOTE(
        id = 5004,
        requiresItem = true,
        consumesItem = true,
        availableForRole = setOf(CULTIST),
        cooldown = MINUTE_IN_MILLIS * 8,
        globalCooldown = true,
    ),

    //clue items 5***
    SEARCH_FOR_INSCRIPTION(
        id = 6001,
        requiresItem = true,
        cooldown = (MINUTE_IN_MILLIS * 1.5).toLong(),
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_SOUND(
        id = 6002,
        requiresItem = true,
        cooldown = (MINUTE_IN_MILLIS * 1.5).toLong(),
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_SCENT(
        id = 6003,
        requiresItem = true,
        cooldown = (MINUTE_IN_MILLIS * 1.5).toLong(),
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_AURA(
        id = 6004,
        requiresItem = true,
        cooldown = (MINUTE_IN_MILLIS * 1.5).toLong(),
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_CORRUPTION(
        id = 6005,
        requiresItem = true,
        cooldown = (MINUTE_IN_MILLIS * 1.5).toLong(),
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_OMEN(
        id = 6006,
        requiresItem = true,
        cooldown = (MINUTE_IN_MILLIS * 1.5).toLong(),
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),
    SEARCH_FOR_DISTORTION(
        id = 6007,
        requiresItem = true,
        cooldown = (MINUTE_IN_MILLIS * 1.5).toLong(),
        active = DEFAULT_INVESTIGATION_ACTIVE
    ),

    //ADVANCED_USEFUL_ITEM 9***
    KINDLE_CLOAK(
        id = 10001,
        requiresItem = true,
        consumesItem = false,
        cooldown = MINUTE_IN_MILLIS * DAY_LENGTH_MINUTES,
        active = MINUTE_IN_MILLIS * NIGHT_LENGTH_MINUTES / 2
    ),
    TOWN_PORTAL_BY_AMULET(
        id = 10002,
        cooldown = MINUTE_IN_MILLIS * 4,
        requiresItem = true,
        consumesItem = false,
    ),
    INVULNERABILITY(
        id = 10003,
        requiresItem = true,
        consumesItem = true,
        active = MINUTE_IN_MILLIS,
        cooldown = (MINUTE_IN_MILLIS * 1.5).toLong(),
    ),

    //advanced cultist items spell 10***
    CLEAN_UP_CLUE(
        id = 11004,
        requiresItem = true,
        consumesItem = true,
        availableForRole = setOf(CULTIST),
        cooldown = SECOND_IN_MILLIS * 10,
        targetTypes = listOf(CLUE),
        range = CLOSE_RANGE
    ),
    ;

    override fun visibilityModifiers(): MutableSet<String> {
        return visibilityModifiersString
    }


    companion object {
        private val abilityMap = values().associateBy { it.id }
        fun byId(abilityId: Int): Ability? {
            return abilityMap[abilityId]
        }
    }
}

fun Int.toAbility() = Ability.byId(this)
