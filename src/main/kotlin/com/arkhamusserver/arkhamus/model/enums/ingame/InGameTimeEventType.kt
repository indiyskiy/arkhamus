package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.DAY_LENGTH_MINUTES
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.GAME_LENGTH
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.NIGHT_LENGTH_MINUTES
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.SECOND_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.TEST_MODE
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues.CorruptionClueHandler

enum class InGameTimeEventType(
    private val defaultTime: Long,
    private val visibility: Visibility,
    private val sourceType: GameObjectType? = null,
    private val targetType: GameObjectType? = null,
) {

    //global
    GAME_END(3 * MINUTE_IN_MILLIS, Visibility.PUBLIC),
    GOD_AWAKEN(GAME_LENGTH * MINUTE_IN_MILLIS, Visibility.PUBLIC), //5 day-night cycles
    DAY(DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS, Visibility.PUBLIC),
    NIGHT(NIGHT_LENGTH_MINUTES * MINUTE_IN_MILLIS, Visibility.PUBLIC),
    SUMMONING_SICKNESS(
        if (TEST_MODE) {
            NIGHT_LENGTH_MINUTES * MINUTE_IN_MILLIS / 8
        } else {
            NIGHT_LENGTH_MINUTES * MINUTE_IN_MILLIS
        },
        Visibility.PUBLIC
    ),

    //altar
    ALTAR_VOTING(DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS / 4, Visibility.PUBLIC),
    FAKE_ALTAR_VOTING(DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS / 4, Visibility.PUBLIC),
    RITUAL_GOING(DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS / 8, Visibility.PUBLIC),
    ALTAR_VOTING_COOLDOWN(DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS / 8, Visibility.PUBLIC),

    //ban vote spot
    CALL_FOR_BAN_VOTE(
        DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS / 8,
        Visibility.PUBLIC,
        sourceType = GameObjectType.CHARACTER,
        targetType = GameObjectType.VOTE_SPOT,
    ),
    FAKE_CALL_FOR_BAN_VOTE(
        DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS / 8,
        Visibility.PUBLIC,
        sourceType = GameObjectType.CHARACTER,
        targetType = GameObjectType.VOTE_SPOT,
    ),

    //ability
    SUMMONED_NIGHT(
        NIGHT_LENGTH_MINUTES * MINUTE_IN_MILLIS / 4,
        Visibility.PUBLIC,
        sourceType = GameObjectType.CHARACTER
    ),
    ABILITY_STUN(
        SECOND_IN_MILLIS * 45,
        Visibility.NONE,
        targetType = GameObjectType.CHARACTER
    ),
    SOUND_CLUE_JAMMER_TURN_OFF(
        MINUTE_IN_MILLIS * 3,
        Visibility.SOURCE,
        targetType = GameObjectType.SOUND_CLUE_JAMMER
    ),
    CORRUPTION_CLUE_GROWTH(
        CorruptionClueHandler.DEFAULT_NULLIFY_TIME,
        Visibility.NONE,
        targetType = GameObjectType.CORRUPTION_CLUE
    ),

    //tech
    TELEPORTATION_STUN(
        SECOND_IN_MILLIS * 3,
        Visibility.NONE,
        targetType = GameObjectType.CHARACTER
    ),
    ;

    fun getDefaultTime() = defaultTime
    fun getVisibility() = visibility
    fun getSourceType() = sourceType
    fun getTargetType() = targetType
}