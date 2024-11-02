package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.GeometryUtils
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ShortTimeEventHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuffs
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithPoint
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class MadnessTickProcessHandler(
    private val curseMadnessLogic: CurseMadnessLogic,
    private val castSomethingMadnessLogic: CastSomethingMadnessLogic,
    private val userLocationHandler: UserLocationHandler,
    private val geometryUtils: GeometryUtils,
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
//                    banSomeone(user, data, timePassedMillis)
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

    private fun craftSomethingMaybe(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        TODO("Not yet implemented")
    }

    private fun castRandomSpellMaybe(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val applyRandom = random.nextInt(50)
        if (applyRandom == 0) {
            castSomethingMadnessLogic.castRandomSpell(user, data, timePassedMillis)
        }
        return false
    }


    private fun canSeeAndInRange(
        user: RedisGameUser,
        point: WithPoint,
        data: GlobalGameData,
        abilityType: Ability
    ): Boolean = (userLocationHandler.userCanSeeTarget(
        user,
        point,
        data.levelGeometryData,
        true
    ) && geometryUtils.distanceLessOrEquals(user, point, abilityType.range)
            )

    private fun curseSomethingMaybe(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val applyRandom = random.nextInt(50)
        if (applyRandom == 0) {
            return curseMadnessLogic.curseSomething(user, data, timePassedMillis)
        }
        return false
    }

}