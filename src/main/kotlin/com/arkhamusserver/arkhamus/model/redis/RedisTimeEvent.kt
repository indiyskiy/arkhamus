package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisTimeEvent")
data class RedisTimeEvent(
    @Id var id: String,
    @Indexed var gameId: Long,
    var sourceObjectId: Long? = null,
    var targetObjectId: Long? = null,
    var timeStart: Long,
    var timePast: Long,
    var timeLeft: Long,
    var type: RedisTimeEventType,
    var state: RedisTimeEventState,
    var xLocation: Double? = null,
    var yLocation: Double? = null,
    var zLocation: Double? = null,
    var visibilityModifiers: MutableList<String> = mutableListOf(),
) : WithVisibilityModifiers {
    override fun visibilityModifiers(): List<VisibilityModifier> {
        return visibilityModifiers.map { enumValueOf<VisibilityModifier>(it) }
    }

    override fun rewriteVisibilityModifiers(modifiers: List<VisibilityModifier>) {
        visibilityModifiers = modifiers.map { it.name }.toMutableList()
    }
}