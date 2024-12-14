package com.arkhamusserver.arkhamus.model.enums.ingame

private const val SECOND: Long = 1000L

enum class ShortTimeEventType(
    private val time: Long,
    private val source: GameObjectType,
    private val visibility: Visibility = Visibility.PUBLIC
) {
    ABILITY_CAST(SECOND, GameObjectType.CHARACTER),
    PEEKABOO_CURSE_ACTIVATED_CONTAINER(SECOND, GameObjectType.CONTAINER),
    PEEKABOO_CURSE_ACTIVATED_CRAFTER(SECOND, GameObjectType.CRAFTER),
    LAST_PERSON_TOUCH_CRAFTER(10 * SECOND, GameObjectType.CRAFTER, Visibility.SOURCE),
    LAST_PERSON_TOUCH_CONTAINER(10 * SECOND, GameObjectType.CRAFTER, Visibility.SOURCE),
    MADNESS_ACT(SECOND, GameObjectType.CHARACTER);

    fun getTime(): Long = time
    fun getSource(): GameObjectType = source
    fun getVisibility(): Visibility = visibility
}