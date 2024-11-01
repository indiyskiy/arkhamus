package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuffs
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.max
import kotlin.random.Random

@Component
class UserMadnessHandler {
    companion object {
        val logger = LoggerFactory.getLogger(UserMadnessHandler::class.java)
        const val NIGHT_MADNESS_TICK_IN_MILLIS = 1.0 / 1000.0
        private val random = Random(System.currentTimeMillis())
    }

    fun applyNightMadness(gameUser: RedisGameUser, timePassedMillis: Long) {
        applyMadness(gameUser, NIGHT_MADNESS_TICK_IN_MILLIS * timePassedMillis)
    }

    fun applyMadness(gameUser: RedisGameUser, madness: Double) {
        val before = gameUser.madness
        gameUser.madness += madness
        val after = gameUser.madness
        val notch = gameUser.madnessNotches.firstOrNull { it >= before && it <= after }
        val notchIndex = notch?.let { gameUser.madnessNotches.indexOf(notch) }
        if (notchIndex != null) {

            gameUser.madnessDebuffs += MadnessDebuffs.values().filter { it.getStepNumber() == notchIndex }
                .random(random).name
        }
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
        val notch = user.madnessNotches.filter { it <= madness }.maxOrNull()
        return notch
    }

}