package com.arkhamusserver.arkhamus.model.enums.ingame

private const val DAY_LENGTH: Long = 8
private const val NIGHT_LENGTH: Long = 4

enum class RedisTimeEventType(
    private val defaultTime: Long,
    private val visibility: Visibility,
) {

    //global
    GAME_END(Long.MAX_VALUE, Visibility.PUBLIC), //as long as possible
    GOD_AWAKEN((DAY_LENGTH + NIGHT_LENGTH) * 60 * 1000 * 5, Visibility.PUBLIC), //5 day-night cycles
    DAY(DAY_LENGTH * 60 * 1000, Visibility.PUBLIC),
    NIGHT(NIGHT_LENGTH * 60 * 1000, Visibility.PUBLIC),

    //altar
    ALTAR_VOTING(DAY_LENGTH * 60 * 1000 / 2, Visibility.PUBLIC),
    RITUAL_GOING((DAY_LENGTH + NIGHT_LENGTH) * 60 * 1000, Visibility.PUBLIC),
    ALTAR_VOTING_COOLDOWN(DAY_LENGTH * 60 * 1000 / 4, Visibility.PUBLIC),

    //ability
    SUMMONED_NIGHT(30 * 1000, Visibility.PUBLIC);

    fun getDefaultTime() = defaultTime
    fun getVisibility() = visibility


}