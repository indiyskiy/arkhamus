package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.model.redis.clues.RedisScentClue

data class CluesContainer(
    var scent: List<RedisScentClue>
)