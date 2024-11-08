package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuffs
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
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
        private val random = Random(System.currentTimeMillis())
    }

    fun processMadness(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ) {
        val madnessDebuffs = user.madnessDebuffs.map { MadnessDebuffs.valueOf(it) }
        var madnessEffectFinal = false
        madnessDebuffs.forEach { debuff ->
            val madnessEffect: Boolean = when (debuff) {
                MadnessDebuffs.BLIND -> {
                    false
                }

                MadnessDebuffs.PSYCHIC_UNSTABLE -> {
                    false
                }

                MadnessDebuffs.CURSED_AURA -> {
                    curseSomethingMaybe(user, data, timePassedMillis)
                }

                MadnessDebuffs.MAGIC_ADDICTED -> {
                    castRandomSpellMaybe(user, data, timePassedMillis)
                }

                MadnessDebuffs.CRAFT_ADDICTED -> {
                    craftSomethingMaybe(user, data, timePassedMillis)
                }

                MadnessDebuffs.BAN_ADDICTED -> {
                    voteForSomeone(user, data, timePassedMillis)
                }

                MadnessDebuffs.LIGHT_ADDICTED -> {
                    lightSomething(user, data, timePassedMillis)
                }

                MadnessDebuffs.UNSTABLE_POSITION -> {
                    teleportMaybe(user, data, timePassedMillis)
                    false
                }

//                MadnessDebuffs.DARK_ENTITY -> {
//                    applyMadnessTickNearby(user, data, timePassedMillis)
//                    false
//                }

                MadnessDebuffs.PROPHET -> {
                    pushGodAwaken(data, timePassedMillis)
                    false
                }
            }
            madnessEffectFinal = madnessEffectFinal || madnessEffect
        }
        if (madnessEffectFinal) {
            shortTimeEventHandler.createShortTimeEvent(
                user.inGameId(),
                data.game.inGameId(),
                data.game.globalTimer,
                ShortTimeEventType.MADNESS_ACT,
                setOf(VisibilityModifier.ALL.name)
            )
        }
    }

    private fun pushGodAwaken(
        data: GlobalGameData,
        timePassedMillis: Long
    ) {
        val godEvent = data.timeEvents.first { it.type == RedisTimeEventType.GOD_AWAKEN }
        eventHandler.pushEvent(godEvent, timePassedMillis)
    }

    private fun teleportMaybe(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val applyRandom = random.nextLong(5000 / timePassedMillis)
        if (applyRandom == 0L) {
            return randomTeleportSomeoneMaybe.teleport(user, data, timePassedMillis)
        }
        return false
    }

    private fun lightSomething(
        user: RedisGameUser,
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
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        return voteMadnessHandler.voteForSomeone(
            user,
            data,
            timePassedMillis
        )
    }

    private fun craftSomethingMaybe(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val applyRandom = random.nextLong(500 / timePassedMillis)
        if (applyRandom == 0L) {
            return craftMadnessLogic.craftSomething(user, data, timePassedMillis)
        }
        return false
    }

    private fun castRandomSpellMaybe(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val applyRandom = random.nextLong(500 / timePassedMillis)
        if (applyRandom == 0L) {
            return castSomethingMadnessLogic.castRandomSpell(user, data, timePassedMillis)
        }
        return false
    }

    private fun curseSomethingMaybe(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val applyRandom = random.nextLong(500 / timePassedMillis)
        if (applyRandom == 0L) {
            return curseMadnessLogic.curseSomething(user, data, timePassedMillis)
        }
        return false
    }

}