package com.arkhamusserver.arkhamus.model.enums.ingame.core

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.DAY_LENGTH_MINUTES
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.NIGHT_LENGTH_MINUTES
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.SECOND_IN_MILLIS
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType.*
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame.*
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers

private const val MINIMUM_COOLDOWN: Long = SECOND_IN_MILLIS
private const val DEFAULT_INVESTIGATION_ACTIVE: Long = MINUTE_IN_MILLIS

private const val CLOSE_RANGE = 5.0
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
    TAKE_FINGERPRINTS(
        id = 1003,
        classBased = true,
        range = CLOSE_RANGE,
        targetTypes = listOf(
            CONTAINER,
            CRAFTER,
        ),
        cooldown = MINUTE_IN_MILLIS * 2 / 3
    ),

    //cultist ability 2***
    FARSIGHT(
        id = 2001,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS * 2,
        active = MINUTE_IN_MILLIS,
    ),
    PARALYSE(
        id = 2002,
        classBased = true,
        cooldown = MINUTE_IN_MILLIS,
        targetTypes = listOf(CHARACTER),
        range = MEDIUM_RANGE
    ),
    MADNESS_LINK(
        id = 2003,
        classBased = true,
        cooldown = 5 * MINUTE_IN_MILLIS,
        active = MINUTE_IN_MILLIS,
        targetTypes = listOf(CHARACTER),
        range = LARGE_RANGE
    ),
    LOCK_DOOR(
        id = 2004,
        classBased = true,
        cooldown = 5 * MINUTE_IN_MILLIS,
        active = SECOND_IN_MILLIS * 45,
        targetTypes = listOf(DOOR),
        range = CLOSE_RANGE
    ),

    //neutral ability 3***
    //useful item ability 4***
    TOWN_PORTAL_BY_SCROLL(
        id = 4002,
        cooldown = MINUTE_IN_MILLIS * 6,
        requiresItem = true,
        consumesItem = true,
    ),
    HEAL_MADNESS_BY_PILL(
        id = 4003,
        classBased = false,
        requiresItem = true,
        consumesItem = true,
        cooldown = MINUTE_IN_MILLIS / 2,
        targetTypes = listOf(CHARACTER),
        range = MEDIUM_RANGE
    ),
    MINOR_DISPELL(
        id = 4004,
        cooldown = MINUTE_IN_MILLIS,
        requiresItem = true,
        consumesItem = true,
        targetTypes = listOf(CONTAINER, CRAFTER),
        range = CLOSE_RANGE
    ),
    PRETEND_CULTIST(
        id = 4005,
        cooldown = MINUTE_IN_MILLIS * 2,
        active = MINUTE_IN_MILLIS,
        requiresItem = true,
        consumesItem = false,
    ),
    CLEAR_MIND(
        id = 4006,
        cooldown = MINUTE_IN_MILLIS * 2,
        requiresItem = true,
        consumesItem = true,
        targetTypes = listOf(CHARACTER, QUEST_GIVER),
        range = CLOSE_RANGE
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

    //clue items 6***
    //INVESTIGATION 2.0
    SEARCH_FOR_SCENT(
        id = 6001,
        requiresItem = true,
        active = DEFAULT_INVESTIGATION_ACTIVE,
        cooldown = DEFAULT_INVESTIGATION_ACTIVE + 1,
        targetTypes = listOf(SCENT_CLUE),
        range = CLOSE_RANGE
    ),
    SEARCH_FOR_SOUND(
        id = 6002,
        requiresItem = true,
        cooldown = (MINUTE_IN_MILLIS * 0.25).toLong(),
        targetTypes = listOf(SOUND_CLUE_JAMMER),
        range = CLOSE_RANGE
    ),
    SEARCH_FOR_OMEN(
        id = 6003,
        requiresItem = true,
        active = DEFAULT_INVESTIGATION_ACTIVE,
        cooldown = (MINUTE_IN_MILLIS * 0.25).toLong(),
        targetTypes = listOf(OMEN_CLUE),
        range = CLOSE_RANGE
    ),
    SEARCH_FOR_CORRUPTION(
        id = 6004,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS,
        range = CLOSE_RANGE,
        targetTypes = listOf(CORRUPTION_CLUE),
    ),
    SEARCH_FOR_AURA(
        id = 6005,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS,
        range = CLOSE_RANGE,
        targetTypes = listOf(AURA_CLUE),
        active = DEFAULT_INVESTIGATION_ACTIVE * 3
    ),
    SEARCH_FOR_DISTORTION(
        id = 6006,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS * 3,
        active = DEFAULT_INVESTIGATION_ACTIVE * 2,
        range = MEDIUM_RANGE,
        targetTypes = listOf(DISTORTION_CLUE),
    ),
    SEARCH_FOR_INSCRIPTION(
        id = 6007,
        requiresItem = true,
        cooldown = MINUTE_IN_MILLIS * 3,
        active = DEFAULT_INVESTIGATION_ACTIVE * 2,
        range = MEDIUM_RANGE,
        targetTypes = listOf(INSCRIPTION_CLUE_GLYPH),
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
    GREAT_DISPELL(
        id = 10004,
        requiresItem = true,
        consumesItem = false,
        cooldown = MINUTE_IN_MILLIS,
        targetTypes = listOf(CONTAINER, CRAFTER),
        range = MEDIUM_RANGE
    ),

    //advanced cultist items spell 11***
    CLEAN_UP_CLUE(
        id = 11004,
        requiresItem = true,
        consumesItem = true,
        availableForRole = setOf(CULTIST),
        cooldown = SECOND_IN_MILLIS * 10,
        targetTypes = listOf(
            SCENT_CLUE,
            SOUND_CLUE,
            OMEN_CLUE,
            AURA_CLUE,
            CORRUPTION_CLUE,
            DISTORTION_CLUE,
            INSCRIPTION_CLUE_GLYPH,
        ),
        range = CLOSE_RANGE
    ),
    DARK_TEMPTATION(
        id = 11005,
        requiresItem = true,
        consumesItem = true,
        availableForRole = setOf(CULTIST),
        active = MINUTE_IN_MILLIS * 8,
        cooldown = MINUTE_IN_MILLIS,
        targetTypes = listOf(QUEST_GIVER),
        range = CLOSE_RANGE
    ),
    ;

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }


    companion object {
        private val abilityMap = values().associateBy { it.id }
        fun byId(abilityId: Int): Ability? {
            return abilityMap[abilityId]
        }
    }
}

fun Int.toAbility() = Ability.byId(this)
