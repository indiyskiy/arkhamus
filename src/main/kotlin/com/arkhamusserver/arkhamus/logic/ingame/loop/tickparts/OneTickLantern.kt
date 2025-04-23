package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings.Companion.MINUTE_IN_MILLIS
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameLanternRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState.*
import org.springframework.stereotype.Component

@Component
class OneTickLantern(
    private val lanternRepository: InGameLanternRepository,
    private val globalGameSettings: GlobalGameSettings,
) {

    fun tickLanterns(data: GlobalGameData, timePassedMillis: Long) {
        val tickDelta = 100.0 / globalGameSettings.nightLengthMinutes / MINUTE_IN_MILLIS
        data.lanterns.forEach {
            when (it.lanternState) {
                EMPTY -> {}
                FILLED -> {}
                LIT -> {
                    it.fuel -= (tickDelta * timePassedMillis)
                    if (it.fuel <= 0) {
                        it.fuel = 0.0
                        it.lanternState = EMPTY
                    }
                    lanternRepository.save(it)
                }
            }
        }
    }

}