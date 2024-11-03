package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuffs
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
    private val shortTimeEventHandler: ShortTimeEventHandler,
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
                    voteForSomeoneMaybe(user, data, timePassedMillis)
                    false
                }

                MadnessDebuffs.LIGHT_ADDICTED -> {
//                    lightSomething(user, data, timePassedMillis)
                    false
                }

                MadnessDebuffs.UNSTABLE_POSITION -> {
//                    teleportMaybe(user, data, timePassedMillis)
                    false
                }

                MadnessDebuffs.DARK_ENTITY -> {
//                    applyMadnessTickNearby(user, data, timePassedMillis)
                    false
                }

                MadnessDebuffs.PROPHET -> {
//                    pushGodAwaken(user, data, timePassedMillis)
                    false
                }
            }
            if (madnessEffect) {
                shortTimeEventHandler.createShortTimeEvent(
                    user.inGameId(),
                    data.game.inGameId(),
                    data.game.globalTimer,
                    ShortTimeEventType.MADNESS_ACT,
                    setOf(VisibilityModifier.ALL.name)
                )
            }
        }
    }

    private fun voteForSomeoneMaybe(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ) {
        voteMadnessHandler.voteForSomeone(
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
            castSomethingMadnessLogic.castRandomSpell(user, data, timePassedMillis)
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