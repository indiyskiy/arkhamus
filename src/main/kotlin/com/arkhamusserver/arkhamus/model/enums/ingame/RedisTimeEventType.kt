package com.arkhamusserver.arkhamus.model.enums.ingame

private const val MINUTES_TO_MILLIS = 60 * 1000
private const val DAY_LENGTH: Long = 8
private const val NIGHT_LENGTH: Long = 4
private const val FULL_DAY: Long = DAY_LENGTH + NIGHT_LENGTH
private const val GAME_LENGTH: Long = FULL_DAY * 5

enum class RedisTimeEventType(
    private val defaultTime: Long,
    private val visibility: Visibility,
) {

    //global
    GAME_END(GAME_LENGTH * MINUTES_TO_MILLIS, Visibility.PUBLIC),
    GOD_AWAKEN(GAME_LENGTH * MINUTES_TO_MILLIS, Visibility.PUBLIC), //5 day-night cycles
    DAY(DAY_LENGTH * MINUTES_TO_MILLIS, Visibility.PUBLIC),
    NIGHT(NIGHT_LENGTH * MINUTES_TO_MILLIS, Visibility.PUBLIC),

    //altar
    ALTAR_VOTING(DAY_LENGTH * MINUTES_TO_MILLIS / 2, Visibility.PUBLIC),
    RITUAL_GOING(DAY_LENGTH * MINUTES_TO_MILLIS / 2, Visibility.PUBLIC),
    ALTAR_VOTING_COOLDOWN(DAY_LENGTH * MINUTES_TO_MILLIS / 4, Visibility.PUBLIC),

    //ability
    SUMMONED_NIGHT(30 * 1000, Visibility.PUBLIC);

    fun getDefaultTime() = defaultTime
    fun getVisibility() = visibility

}