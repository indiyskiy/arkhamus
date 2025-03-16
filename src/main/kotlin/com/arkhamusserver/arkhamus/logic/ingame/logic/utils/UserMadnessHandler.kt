package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ActivityHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuffs
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
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

    fun applyNightMadness(
        gameUser: InGameUser,
        timePassedMillis: Long,
        gameTime: Long,
        globalGameData: GlobalGameData
    ) {
        tryApplyMadness(gameUser, NIGHT_MADNESS_TICK_IN_MILLIS * timePassedMillis, gameTime, globalGameData)
    }

    fun tryApplyMadness(
        gameUser: InGameUser,
        madness: Double,
        gameTime: Long,
        globalGameData: GlobalGameData
    ) {
        if(gameUser.stateTags.contains(UserStateTag.MADNESS_LINK_SOURCE)){
            val realTarget = globalGameData.users.values.firstOrNull{
                it.stateTags.contains(UserStateTag.MADNESS_LINK_TARGET)
            }?:gameUser
            realApplyMadness(realTarget, madness, gameTime)
            return
        }
        realApplyMadness(gameUser, madness, gameTime)
    }

    private fun realApplyMadness(
        gameUser: InGameUser,
        madness: Double,
        gameTime: Long
    ) {
        val before = gameUser.additionalData.madness.madness
        val modifier = if (gameUser.additionalData.madness.madnessDebuffs.contains(MadnessDebuffs.PSYCHIC_UNSTABLE.name)) 1.5 else 1.0
        gameUser.additionalData.madness.madness += (madness * modifier)
        val after = gameUser.additionalData.madness.madness
        applyMadnessDebuffMaybe(gameUser, before, after, gameTime)
    }

    private fun applyMadnessDebuffMaybe(
        gameUser: InGameUser,
        before: Double,
        after: Double,
        gameTime: Long
    ) {
        val notch = gameUser.additionalData.madness.madnessNotches.firstOrNull { it >= before && it <= after }
        val notchIndex = notch?.let { gameUser.additionalData.madness.madnessNotches.indexOf(notch) }
        if (notchIndex != null) {
            applyMadnessDebuff(notchIndex, gameUser, gameTime)
        }
    }

    private fun applyMadnessDebuff(
        notchIndex: Int,
        gameUser: InGameUser,
        gameTime: Long
    ) {
        val debuff = MadnessDebuffs.values().filter { it.getStepNumber() == notchIndex }
            .random(random).name
        gameUser.additionalData.madness.madnessDebuffs += debuff
        activityHandler.addUserNotTargetActivity(
            gameId = gameUser.gameId,
            activityType = ActivityType.USER_GOT_MAD,
            sourceUser = gameUser,
            gameTime = gameTime,
            relatedEventId = notchIndex.toLong()
        )
    }

    fun filterNotMad(gameUsers: Collection<InGameUser>): List<InGameUser> =
        gameUsers.filterNot { isCompletelyMad(it) }

    fun isCompletelyMad(gameUser: InGameUser): Boolean =
        gameUser.additionalData.madness.madness >= gameUser.additionalData.madness.madnessNotches.max()

    fun reduceMadness(user: InGameUser, reduceValue: Double) {
        val notch = currentMinNotch(user)
        notch?.let {
            val afterReduced = max(user.additionalData.madness.madness - reduceValue, notch)
            user.additionalData.madness.madness = afterReduced
        }
    }

    private fun currentMinNotch(user: InGameUser): Double? {
        val madness = user.additionalData.madness.madness
        val notch = user.additionalData.madness.madnessNotches.filter { it <= madness }.maxOrNull()
        return notch
    }

}