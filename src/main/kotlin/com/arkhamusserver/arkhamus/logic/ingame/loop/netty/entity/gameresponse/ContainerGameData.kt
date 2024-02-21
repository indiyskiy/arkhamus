package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

class ContainerGameData(
    var container: RedisContainer,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    tick: Long
) : GameUserData(gameUser, otherGameUsers, tick)