package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser

class HeartbeatGameData(
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    tick: Long
) : GameUserData(gameUser, otherGameUsers, tick)