package com.arkhamusserver.arkhamus.model.enums.ingame

private const val SECOND: Long = 1000L

enum class ShortTimeEventType(
    private val time: Long,
    private val source: GameObjectType,
) {
    ABILITY_CAST(SECOND / 2, GameObjectType.CHARACTER);

    fun getTime(): Long = time
    fun getSource(): GameObjectType = source
}