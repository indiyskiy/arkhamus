package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithStringId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisClue")
data class RedisClue(
    @Id
    var id: String,
    @Indexed
    var gameId: Long,
    var levelZoneId: Long,
    var clue: Clue,
    var visibilityModifiers: MutableSet<String>,
) : WithVisibilityModifiers, WithStringId {
    override fun visibilityModifiers(): MutableSet<String> {
        return visibilityModifiers
    }

    override fun stringId(): String {
        return id
    }
}