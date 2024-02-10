package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.model.redis.RedisContainer
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

data class ContainerGameResponse(
    var container: RedisContainer,
    var gameUser: RedisGameUser
) : GameUserResponseMessage {
    override fun gameUser(): RedisGameUser = gameUser

}