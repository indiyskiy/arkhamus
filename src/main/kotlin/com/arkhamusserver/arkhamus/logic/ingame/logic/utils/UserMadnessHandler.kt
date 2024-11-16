package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuffs
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.max
import kotlin.random.Random

@Component
class UserMadnessHandler(
    private val activityHandler: ActivityHandler
) {
    companion object {
        val logger = LoggerFactory.getLogger(UserMadnessHandler::class.java)
        const val NIGHT_MADNESS_TICK_IN_MILLIS = 1.0 / 1000.0
        private val random = Random(System.currentTimeMillis())
    }

    fun applyNightMadness(gameUser: RedisGameUser, timePassedMillis: Long, gameTime: Long) {
        applyMadness(gameUser, NIGHT_MADNESS_TICK_IN_MILLIS * timePassedMillis, gameTime)
    }

    fun applyMadness(gameUser: RedisGameUser, madness: Double, gameTime: Long) {
        val before = gameUser.madness
        val modifier = if (gameUser.madnessDebuffs.contains(MadnessDebuffs.PSYCHIC_UNSTABLE.name)) 1.5 else 1.0
        gameUser.madness += (madness * modifier)
        val after = gameUser.madness
        applyMadnessDebuffMaybe(gameUser, before, after, gameTime)
    }

    private fun applyMadnessDebuffMaybe(
        gameUser: RedisGameUser,
        before: Double,
        after: Double,
        gameTime: Long
    ) {
        val notch = gameUser.madnessNotches.firstOrNull { it >= before && it <= after }
        val notchIndex = notch?.let { gameUser.madnessNotches.indexOf(notch) }
        if (notchIndex != null) {
            applyMadnessDebuff(notchIndex, gameUser, gameTime)
        }
    }

    private fun applyMadnessDebuff(
        notchIndex: Int,
        gameUser: RedisGameUser,
        gameTime: Long
    ) {
        val debuff = MadnessDebuffs.values().filter { it.getStepNumber() == notchIndex }
            .random(random).name
        gameUser.madnessDebuffs += debuff
        logger.info("apply debuff ${debuff} to ${gameUser.userId}-${gameUser.nickName}")
        activityHandler.addUserNotTargetActivity(
            gameId = gameUser.gameId,
            activityType = ActivityType.USER_GOT_MAD,
            sourceUser = gameUser,
            gameTime = gameTime,
            relatedEventId = notchIndex.toLong()
        )
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