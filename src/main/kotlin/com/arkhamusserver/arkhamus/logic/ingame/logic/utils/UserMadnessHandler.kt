package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuffs
import com.arkhamusserver.arkhamus.model.ingame.InGameGameUser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.max
import kotlin.random.Random

@Component
class UserMadnessHandler(
    private val activityHandler: ActivityHandler
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(UserMadnessHandler::class.java)
        const val NIGHT_MADNESS_TICK_IN_MILLIS = 1.0 / 1000.0
        private val random = Random(System.currentTimeMillis())
    }

    fun applyNightMadness(gameUser: InGameGameUser, timePassedMillis: Long, gameTime: Long) {
        applyMadness(gameUser, NIGHT_MADNESS_TICK_IN_MILLIS * timePassedMillis, gameTime)
    }

    fun applyMadness(gameUser: InGameGameUser, madness: Double, gameTime: Long) {
        val before = gameUser.madness
        val modifier = if (gameUser.madnessDebuffs.contains(MadnessDebuffs.PSYCHIC_UNSTABLE.name)) 1.5 else 1.0
        gameUser.madness += (madness * modifier)
        val after = gameUser.madness
        applyMadnessDebuffMaybe(gameUser, before, after, gameTime)
    }

    private fun applyMadnessDebuffMaybe(
        gameUser: InGameGameUser,
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
        gameUser: InGameGameUser,
        gameTime: Long
    ) {
        val debuff = MadnessDebuffs.values().filter { it.getStepNumber() == notchIndex }
            .random(random).name
        gameUser.madnessDebuffs += debuff
        logger.info("apply debuff $debuff to ${gameUser.inGameId()}-${gameUser.nickName}")
        activityHandler.addUserNotTargetActivity(
            gameId = gameUser.gameId,
            activityType = ActivityType.USER_GOT_MAD,
            sourceUser = gameUser,
            gameTime = gameTime,
            relatedEventId = notchIndex.toLong()
        )
    }

    fun filterNotMad(gameUsers: Collection<InGameGameUser>): List<InGameGameUser> =
        gameUsers.filterNot { isCompletelyMad(it) }

    fun isCompletelyMad(gameUser: InGameGameUser): Boolean =
        gameUser.madness >= gameUser.madnessNotches.max()

    fun reduceMadness(user: InGameGameUser, reduceValue: Double) {
        val notch = currentMinNotch(user)
        notch?.let {
            val afterReduced = max(user.madness - reduceValue, notch)
            user.madness = afterReduced
        }
    }

    private fun currentMinNotch(user: InGameGameUser): Double? {
        val madness = user.madness
        val notch = user.madnessNotches.filter { it <= madness }.maxOrNull()
        return notch
    }

}