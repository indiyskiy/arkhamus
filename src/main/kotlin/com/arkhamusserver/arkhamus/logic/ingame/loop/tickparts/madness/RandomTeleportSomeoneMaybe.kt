package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.TeleportHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.ThresholdType
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class RandomTeleportSomeoneMaybe(
    private val teleportHandler: TeleportHandler
) {

    companion object {
        private val random = Random(System.currentTimeMillis())
    }

    fun teleport(
        user: InGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val stunned = user.stateTags.contains(UserStateTag.STUN)
        if (stunned) return false
        val possiblePlaces = findPlaces(data)
        if (possiblePlaces.isEmpty()) return false
        val place = possiblePlaces.random(random)
        teleportHandler.forceTeleport(data.game, user, place)
        return true
    }

    private fun findPlaces(
        data: GlobalGameData,
    ): List<WithPoint> {
        val threshold: List<WithPoint> = data.thresholds.filter { it.type == ThresholdType.BAN }
        val altar: WithPoint = data.altarHolder!!
        return threshold + altar
    }

}