package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

open class GameUserResponseMessage(
    val gameUser: RedisGameUser?,
    val otherGameUsers: List<RedisGameUser> = emptyList(),
) : GameResponseMessage