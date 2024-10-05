package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithId
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithVisibilityModifiers
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisGameUser")
data class RedisGameUser(
    @Id var id: String,
    @Indexed var userId: Long,
    var nickName: String,
    var role: RoleTypeInGame,
    var classInGame: ClassInGame,
    @Indexed var gameId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var madness: Double,
    var madnessNotches: List<Double>,
    var items: MutableMap<Int, Int> = HashMap(),
    var stateTags: MutableSet<String> = mutableSetOf(),
    var madnessDebuffs: MutableSet<String> = mutableSetOf(),
    var callToArms: Int,
    //tech
    var won: Boolean? = null,
    var sawTheEndOfTimes: Boolean = false,
    var connected: Boolean,
    var leftTheGame: Boolean = false,
    var visibilityModifiers: MutableList<String>,
) : WithPoint, WithId, WithVisibilityModifiers {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }

    override fun inGameId(): Long {
        return userId
    }

    override fun visibilityModifiers(): List<VisibilityModifier> {
        return visibilityModifiers.map { enumValueOf<VisibilityModifier>(it) }
    }

    override fun rewriteVisibilityModifiers(modifiers: List<VisibilityModifier>) {
        visibilityModifiers = modifiers.map { it.name }.toMutableList()
    }
}