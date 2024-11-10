package com.arkhamusserver.arkhamus.model.enums.ingame

private const val SECOND: Long = 1000L

enum class ShortTimeEventType(
    private val time: Long,
    private val source: GameObjectType,
) {
    ABILITY_CAST(SECOND, GameObjectType.CHARACTER),
    PEEKABOO_CURSE_ACTIVATED_CONTAINER(SECOND, GameObjectType.CONTAINER),
    PEEKABOO_CURSE_ACTIVATED_CRAFTER(SECOND, GameObjectType.CRAFTER),
    MADNESS_ACT(SECOND, GameObjectType.CHARACTER);

    fun getTime(): Long = time
    fun getSource(): GameObjectType = source
}