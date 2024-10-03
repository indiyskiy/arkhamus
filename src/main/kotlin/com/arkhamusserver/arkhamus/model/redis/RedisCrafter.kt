package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithGameTags
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisCrafter")
data class RedisCrafter(
    @Id var id: String,
    @Indexed var gameId: Long,
    var crafterId: Long,
    var holdingUser: Long? = null,
    var state: MapObjectState = MapObjectState.ACTIVE,
    var x: Double,
    var y: Double,
    var z: Double,
    var interactionRadius: Double = 0.0,
    var items: MutableMap<Int, Int> = HashMap(),
    var crafterType: CrafterType,
    var gameTags: MutableList<String>,
) : WithPoint, WithGameTags {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }

    override fun gameTags(): List<InGameObjectTag> {
        return gameTags.map { enumValueOf<InGameObjectTag>(it) }
    }

    override fun rewriteGameTags(tags: List<InGameObjectTag>) {
        gameTags = tags.map { it.name }.toMutableList()
    }
}