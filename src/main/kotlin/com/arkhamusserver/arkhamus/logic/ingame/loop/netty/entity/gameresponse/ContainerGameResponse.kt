package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gameresponse

import com.arkhamusserver.arkhamus.model.redis.RedisContainer

data class ContainerGameResponse(
    var container: RedisContainer
) : GameResponseMessage