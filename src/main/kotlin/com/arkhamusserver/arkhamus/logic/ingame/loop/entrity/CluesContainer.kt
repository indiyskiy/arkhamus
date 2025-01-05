package com.arkhamusserver.arkhamus.logic.ingame.loop.entrity

import com.arkhamusserver.arkhamus.model.redis.clues.RedisScentClue
import com.arkhamusserver.arkhamus.model.redis.clues.RedisSoundClue

data class CluesContainer(
    var scent: List<RedisScentClue>,
    var sound: List<RedisSoundClue>
)