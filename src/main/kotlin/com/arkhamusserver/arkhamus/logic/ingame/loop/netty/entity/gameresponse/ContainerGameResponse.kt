package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

class ContainerGameResponse(
    var container: RedisContainer,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>
) : GameUserResponseMessage(gameUser, otherGameUsers) 