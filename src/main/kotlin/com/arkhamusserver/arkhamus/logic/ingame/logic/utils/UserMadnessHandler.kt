package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component
import kotlin.math.max

@Component
class UserMadnessHandler {
    companion object {
        const val NIGHT_MADNESS_TICK = 1.0 * ArkhamusOneTickLogic.TICK_DELTA / 1000.0
    }

    fun applyNightMadness(gameUser: RedisGameUser) {
        gameUser.madness += NIGHT_MADNESS_TICK
    }

    fun filterNotMad(gameUsers: Collection<RedisGameUser>): List<RedisGameUser> =
        gameUsers.filterNot { isCompletelyMad(it) }

    fun isCompletelyMad(gameUser: RedisGameUser): Boolean =
        gameUser.madness >= gameUser.madnessNotches.max()

    fun reduceMadness(user: RedisGameUser, reduceValue: Double) {
        val notch = currentMinNotch(user)
        notch?.let {
            val afterReduced = max(user.madness - reduceValue, notch)
            user.madness = afterReduced
        }
    }

    private fun currentMinNotch(user: RedisGameUser): Double? {
        val madness = user.madness
        val notch = user.madnessNotches.sorted().firstOrNull { it >= madness }
        return notch
    }

}