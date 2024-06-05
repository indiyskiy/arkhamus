package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class UserMadnessHandler {
    companion object {
        const val NIGHT_MADNESS_TICK = 1.0 / 1000.0 * ArkhamusOneTickLogic.TICK_DELTA
    }

    fun applyNightMadness(gameUser: RedisGameUser) {
        gameUser.madness += NIGHT_MADNESS_TICK
    }
}