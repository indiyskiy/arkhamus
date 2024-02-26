package com.arkhamusserver.arkhamus.model.enums.ingame

enum class RedisTimeEventType(
    private val defaultTime: Long,
    private val visibility: Visibility,
) {
    DAY(8 * 60 * 1000, Visibility.PUBLIC),
    NIGHT(4 * 60 * 1000, Visibility.PUBLIC),
    SUMMONED_NIGHT(30 * 1000, Visibility.PUBLIC);

    fun getDefaultTime() = defaultTime
    fun getVisibility() = visibility
}