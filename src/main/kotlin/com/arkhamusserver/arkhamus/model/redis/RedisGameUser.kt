package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame
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
    var x: Double = 0.0,
    var y: Double = 0.0,
    var madness: Double,
    var madnessNotches: List<Double>,
    var items: MutableMap<Int, Int> = HashMap(),
    var stateTags: MutableSet<String> = mutableSetOf()
)