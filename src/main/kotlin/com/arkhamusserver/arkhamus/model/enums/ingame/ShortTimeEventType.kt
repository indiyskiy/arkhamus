package com.arkhamusserver.arkhamus.model.enums.ingame

private const val SECOND: Long = 1000L

enum class ShortTimeEventType(
    private val time: Long,
    private val source: GameObjectType,
) {
    ABILITY_CAST(SECOND / 2, GameObjectType.CHARACTER),
    PEEKABOO_CURSE_ACTIVATED_CONTAINER(SECOND / 2, GameObjectType.CONTAINER),
    PEEKABOO_CURSE_ACTIVATED_CRAFTER(SECOND / 2, GameObjectType.CRAFTER);

    fun getTime(): Long = time
    fun getSource(): GameObjectType = source
}