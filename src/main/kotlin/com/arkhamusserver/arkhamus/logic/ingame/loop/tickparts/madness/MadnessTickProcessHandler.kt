package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuff
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class MadnessTickProcessHandler(
    private val curseMadnessLogic: CurseMadnessLogic,
    private val castSomethingMadnessLogic: CastSomethingMadnessLogic,
    private val craftMadnessLogic: CraftMadnessLogic,
    private val voteMadnessHandler: VoteMadnessHandler,
    private val lightMadnessHandler: LightMadnessHandler,
    private val shortTimeEventHandler: ShortTimeEventHandler,
    private val randomTeleportSomeoneMaybe: RandomTeleportSomeoneMaybe,
    private val eventHandler: TimeEventHandler,
) {

    companion object {
        const val ONCE_PER_TWO_MINUTES = 120000
        const val ONCE_PER_MINUTE = 60000
        const val TWICE_PER_MINUTE = 30000
        const val FOUR_TIMES_PER_MINUTE = 15000
        const val EVERY_TIME_POSSIBLE = 3750
        private val random = Random(System.currentTimeMillis())
    }

    fun processMadness(
        user: InGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ) {
        val timePassedMillisZeroSafe = timePassedMillis.coerceAtLeast(1)
        val madnessDebuffs = user.additionalData.madness.madnessDebuffs
        var madnessEffectFinal = false
        madnessDebuffs.forEach { debuff ->
            val madnessEffect: Boolean = when (debuff) {
                MadnessDebuff.BLIND -> {
                    processBlindMadness(user)
                }

                MadnessDebuff.PSYCHIC_UNSTABLE -> {
                    false
                }

                MadnessDebuff.CURSED_AURA -> {
                    curseSomethingMaybe(user, data, timePassedMillisZeroSafe)
                }

                MadnessDebuff.MAGIC_ADDICTED -> {
                    castRandomSpellMaybe(user, data, timePassedMillisZeroSafe)
                }

                MadnessDebuff.CRAFT_ADDICTED -> {
                    craftSomethingMaybe(user, data, timePassedMillisZeroSafe)
                }

                MadnessDebuff.BAN_ADDICTED -> {
                    voteForSomeone(user, data)
                }

                MadnessDebuff.LIGHT_ADDICTED -> {
                    lightSomething(user, data, timePassedMillisZeroSafe)
                }

                MadnessDebuff.UNSTABLE_POSITION -> {
                    teleportMaybe(user, data, timePassedMillisZeroSafe)
                    false
                }

//                MadnessDebuffs.DARK_ENTITY -> {
//                    applyMadnessTickNearby(user, data, timePassedMillisZeroSafe)
//                    false
//                }

                MadnessDebuff.PROPHET -> {
                    pushGodAwaken(data, timePassedMillisZeroSafe)
                    false
                }
            }
            madnessEffectFinal = madnessEffectFinal || madnessEffect
        }
        if (madnessEffectFinal) {
            shortTimeEventHandler.createShortTimeEvent(
                objectId = user.inGameId(),
                gameId = data.game.inGameId(),
                globalTimer = data.game.globalTimer,
                type = ShortTimeEventType.MADNESS_ACT,
                visibilityModifiers = setOf(VisibilityModifier.ALL),
                data = data,
                sourceUserId = user.inGameId()
            )
        }
    }

    private fun pushGodAwaken(
        data: GlobalGameData,
        timePassedMillis: Long
    ) {
        val godEvent = data.timeEvents.first { it.type == InGameTimeEventType.GOD_AWAKEN }
        eventHandler.pushEvent(godEvent, timePassedMillis / 2)
    }

    private fun processBlindMadness(user: InGameUser): Boolean {
        user.currentVisibilityLength /= 0.75
        return false
    }

    private fun teleportMaybe(
        user: InGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val applyRandom = random.nextLong(ONCE_PER_TWO_MINUTES / timePassedMillis)
        if (applyRandom == 0L) {
            return randomTeleportSomeoneMaybe.teleport(user, data, timePassedMillis)
        }
        return false
    }

    private fun lightSomething(
        user: InGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        return lightMadnessHandler.lightSomething(
            user,
            data,
            timePassedMillis
        )
    }

    private fun voteForSomeone(
        user: InGameUser,
        data: GlobalGameData,
    ): Boolean {
        return voteMadnessHandler.voteForSomeone(
            user,
            data,
        )
    }

    private fun craftSomethingMaybe(
        user: InGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val applyRandom = random.nextLong(TWICE_PER_MINUTE / timePassedMillis)
        if (applyRandom == 0L) {
            return craftMadnessLogic.craftSomething(user, data)
        }
        return false
    }

    private fun castRandomSpellMaybe(
        user: InGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val applyRandom = random.nextLong(FOUR_TIMES_PER_MINUTE / timePassedMillis)
        if (applyRandom == 0L) {
            return castSomethingMadnessLogic.castRandomSpell(user, data)
        }
        return false
    }

    private fun curseSomethingMaybe(
        user: InGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val applyRandom = random.nextLong(TWICE_PER_MINUTE / timePassedMillis)
        if (applyRandom == 0L) {
            return curseMadnessLogic.curseSomething(user, data, timePassedMillis)
        }
        return false
    }

}