package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness.MadnessTickProcessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.tickuser.OneTickUserInventory
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OneTickUser(
    private val madnessTickProcessHandler: MadnessTickProcessHandler,
    private val oneTickUserInventory: OneTickUserInventory
) {

    companion object {
        private val logger = LoggingUtils.getLogger<OneTickUser>()
        const val POTATO_MADNESS_TICK_MILLIS: Double = 2.0 / 1000.0
    }

    fun processUsers(data: GlobalGameData, timePassedMillis: Long) {
        data.users.forEach { user ->
            user.value.currentVisibilityLength = user.value.initialVisibilityLength
            user.value.currentCooldownSpeed = user.value.initialCooldownSpeed
            user.value.currentMovementSpeed = user.value.initialMovementSpeed
            processUser(user.value, data, timePassedMillis)
        }
    }

    private fun processUser(
        user: InGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ) {
        oneTickUserInventory.processInventory(user, data, timePassedMillis, data.game.globalTimer)
        processMadness(user, data, timePassedMillis)
    }

    private fun processMadness(
        user: InGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ) {
        madnessTickProcessHandler.processMadness(
            user = user,
            data = data,
            timePassedMillis = timePassedMillis
        )
    }
}