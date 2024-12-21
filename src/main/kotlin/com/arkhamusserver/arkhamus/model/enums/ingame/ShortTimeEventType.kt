package com.arkhamusserver.arkhamus.model.enums.ingame

private const val SECOND: Long = 1000L
private const val MIDDLE: Long = 10 * SECOND

enum class ShortTimeEventType(
    private val time: Long,
    private val source: GameObjectType,
    private val visibility: Visibility = Visibility.PUBLIC
) {
    ABILITY_CAST(SECOND, GameObjectType.CHARACTER),
    PEEKABOO_CURSE_ACTIVATED_CONTAINER(SECOND, GameObjectType.CONTAINER),
    PEEKABOO_CURSE_ACTIVATED_CRAFTER(SECOND, GameObjectType.CRAFTER),
    LAST_PERSON_TOUCH_CRAFTER(MIDDLE, GameObjectType.CRAFTER, Visibility.SOURCE),
    LAST_PERSON_TOUCH_CONTAINER(MIDDLE, GameObjectType.CONTAINER, Visibility.SOURCE),
    MADNESS_ACT(SECOND, GameObjectType.CHARACTER),
    SCENT_OBJECT_CHECK(MIDDLE, GameObjectType.SCENT_OBJECT, Visibility.SOURCE);

    fun getTime(): Long = time
    fun getSource(): GameObjectType = source
    fun getVisibility(): Visibility = visibility
}