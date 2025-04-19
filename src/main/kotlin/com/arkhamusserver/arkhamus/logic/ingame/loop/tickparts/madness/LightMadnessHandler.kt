package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.LanternHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.ingame.InGameLantern
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component

@Component
class LightMadnessHandler(
    private val userLocationHandler: UserLocationHandler,
    private val lanternHandler: LanternHandler,
) {

    fun lightSomething(
        user: InGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ): Boolean {
        val lanterns = lanterns(data, user)
        var didSomething = false
        if (lanterns.isNotEmpty()) {
            lanterns.forEach { lantern ->
                if (lanternHandler.canFill(user, lantern)) {
                    lanternHandler.fillLantern(user, lantern)
                    didSomething = true
                }
                if (lanternHandler.canLight(lantern)) {
                    lanternHandler.lightLantern(lantern)
                    didSomething = true
                }
            }
        }
        return didSomething
    }

    private fun lanterns(
        data: GlobalGameData,
        user: InGameUser
    ): List<InGameLantern> = data.lanterns.filter {
        userLocationHandler.userCanSeeTarget(
            user,
            it,
            data.levelGeometryData,
            true
        ) && userLocationHandler.userInInteractionRadius(user, it)
    }.toList()
}