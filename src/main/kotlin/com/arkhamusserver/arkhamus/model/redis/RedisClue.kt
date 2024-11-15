package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers

data class RedisClue(
    override var id: String,
    override var gameId: Long,
    var levelZoneId: Long,
    var clue: Clue,
    var visibilityModifiers: MutableSet<String>,
) : RedisGameEntity, WithVisibilityModifiers, WithStringId {
    override fun visibilityModifiers(): MutableSet<String> {
        return visibilityModifiers
    }

    override fun stringId(): String {
        return id
    }
}