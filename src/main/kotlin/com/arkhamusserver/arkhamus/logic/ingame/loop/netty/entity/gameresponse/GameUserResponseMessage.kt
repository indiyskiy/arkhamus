package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

interface GameUserResponseMessage: GameResponseMessage{
    fun gameUser(): RedisGameUser?
}