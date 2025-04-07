package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType.*
import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility.PUBLIC
import com.arkhamusserver.arkhamus.model.enums.ingame.Visibility.SOURCE

private const val SECOND: Long = 1000L
private const val MIDDLE: Long = 10 * SECOND

enum class ShortTimeEventType(
    private val time: Long,
    private val source: GameObjectType,
    private val visibility: Visibility = PUBLIC
) {
    ABILITY_CAST(SECOND, CHARACTER),
    INCLUSION_TRIGGERED(MIDDLE, CHARACTER),
    PEEKABOO_CURSE_ACTIVATED_CONTAINER(SECOND, CONTAINER),
    PEEKABOO_CURSE_ACTIVATED_CRAFTER(SECOND, CRAFTER),
    LAST_PERSON_TOUCH_CRAFTER(MIDDLE, CRAFTER, SOURCE),
    LAST_PERSON_TOUCH_CONTAINER(MIDDLE, CONTAINER, SOURCE),
    MADNESS_ACT(SECOND, CHARACTER);

    fun getTime(): Long = time
    fun getSource(): GameObjectType = source
    fun getVisibility(): Visibility = visibility
}