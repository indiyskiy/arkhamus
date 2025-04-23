package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.SECOND_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.CorruptionClueHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.TimeBase.*

enum class InGameTimeEventType(
    private val defaultTimeBase: TimeBase,
    private val defaultTimeMultiplier: Long,
    private val visibility: Visibility,
    private val sourceType: GameObjectType? = null,
    private val targetType: GameObjectType? = null,
) {

    //global
    GAME_END(
        PLAIN_BASE,
        3 * MINUTE_IN_MILLIS,
        Visibility.PUBLIC
    ),
    GOD_AWAKEN(
        GAME_LENGTH_BASE,
        MINUTE_IN_MILLIS,
        Visibility.PUBLIC
    ),
    DAY(DAY_LENGTH_BASE, MINUTE_IN_MILLIS, Visibility.PUBLIC),
    NIGHT(NIGHT_LENGTH_BASE, MINUTE_IN_MILLIS, Visibility.PUBLIC),
    SUMMONING_SICKNESS(
        NIGHT_LENGTH_BASE,
        MINUTE_IN_MILLIS / 8,
        Visibility.PUBLIC
    ),

    //altar
    ALTAR_VOTING(DAY_LENGTH_BASE, MINUTE_IN_MILLIS / 4, Visibility.PUBLIC),
    FAKE_ALTAR_VOTING(DAY_LENGTH_BASE, MINUTE_IN_MILLIS / 4, Visibility.PUBLIC),
    RITUAL_GOING(DAY_LENGTH_BASE, MINUTE_IN_MILLIS / 8, Visibility.PUBLIC),
    ALTAR_VOTING_COOLDOWN(DAY_LENGTH_BASE, MINUTE_IN_MILLIS / 8, Visibility.PUBLIC),

    //ban vote spot
    CALL_FOR_BAN_VOTE(
        DAY_LENGTH_BASE, MINUTE_IN_MILLIS / 8,
        Visibility.PUBLIC,
        sourceType = GameObjectType.CHARACTER,
        targetType = GameObjectType.VOTE_SPOT,
    ),
    FAKE_CALL_FOR_BAN_VOTE(
        DAY_LENGTH_BASE, MINUTE_IN_MILLIS / 8,
        Visibility.PUBLIC,
        sourceType = GameObjectType.CHARACTER,
        targetType = GameObjectType.VOTE_SPOT,
    ),

    //ability
    SUMMONED_NIGHT(
        NIGHT_LENGTH_BASE, MINUTE_IN_MILLIS / 4,
        Visibility.PUBLIC,
        sourceType = GameObjectType.CHARACTER
    ),
    ABILITY_STUN(
        PLAIN_BASE,
        SECOND_IN_MILLIS * 30,
        Visibility.NONE,
        targetType = GameObjectType.CHARACTER
    ),
    SOUND_CLUE_JAMMER_TURN_OFF(
        PLAIN_BASE,
        MINUTE_IN_MILLIS * 3,
        Visibility.SOURCE,
        targetType = GameObjectType.SOUND_CLUE_JAMMER
    ),
    CORRUPTION_CLUE_GROWTH(
        PLAIN_BASE,
        CorruptionClueHandler.DEFAULT_NULLIFY_TIME,
        Visibility.NONE,
        targetType = GameObjectType.CORRUPTION_CLUE
    ),

    //tech
    TELEPORTATION_STUN(
        PLAIN_BASE,
        SECOND_IN_MILLIS * 3,
        Visibility.NONE,
        targetType = GameObjectType.CHARACTER
    ),
    ;

    fun getTimeBase() = defaultTimeBase
    fun getTimeMultiplier() = defaultTimeMultiplier
    fun getVisibility() = visibility
    fun getSourceType() = sourceType
    fun getTargetType() = targetType
}
