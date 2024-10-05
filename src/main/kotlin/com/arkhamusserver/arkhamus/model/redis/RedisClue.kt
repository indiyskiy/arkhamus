package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
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
    var visibilityModifiers: MutableList<String> = mutableListOf(),
) : WithVisibilityModifiers {
    override fun visibilityModifiers(): List<VisibilityModifier> {
        return visibilityModifiers.map { enumValueOf<VisibilityModifier>(it) }
    }

    override fun rewriteVisibilityModifiers(modifiers: List<VisibilityModifier>) {
        visibilityModifiers = modifiers.map { it.name }.toMutableList()
    }
}