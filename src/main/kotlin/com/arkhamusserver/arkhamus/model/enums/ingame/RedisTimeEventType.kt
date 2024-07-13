package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.DAY_LENGTH_MINUTES
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.GAME_LENGTH
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.NIGHT_LENGTH_MINUTES

enum class RedisTimeEventType(
    private val defaultTime: Long,
    private val visibility: Visibility,
) {

    //global
    GAME_END(GAME_LENGTH * MINUTE_IN_MILLIS, Visibility.PUBLIC),
    GOD_AWAKEN(GAME_LENGTH * MINUTE_IN_MILLIS, Visibility.PUBLIC), //5 day-night cycles
    DAY(DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS, Visibility.PUBLIC),
    NIGHT(NIGHT_LENGTH_MINUTES * MINUTE_IN_MILLIS, Visibility.PUBLIC),

    //altar
    ALTAR_VOTING(DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS / 2, Visibility.PUBLIC),
    RITUAL_GOING(DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS / 2, Visibility.PUBLIC),
    ALTAR_VOTING_COOLDOWN(DAY_LENGTH_MINUTES * MINUTE_IN_MILLIS / 4, Visibility.PUBLIC),

    //ability
    SUMMONED_NIGHT(30 * 1000, Visibility.PUBLIC);

    fun getDefaultTime() = defaultTime
    fun getVisibility() = visibility

}