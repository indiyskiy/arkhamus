package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.GlobalGameSettings
import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisLanternRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.LanternState.*
import org.springframework.stereotype.Component

@Component
class OneTickLantern(
    private val lanternRepository: RedisLanternRepository
) {

    companion object {
        const val TICK_DELTA = 100.0 /
                GlobalGameSettings.NIGHT_LENGTH_MINUTES /
                GlobalGameSettings.MINUTE_IN_MILLIS *
                ArkhamusOneTickLogic.TICK_DELTA
    }

    fun tick(data: GlobalGameData) {
        data.lanterns.forEach {
            when (it.lanternState) {
                EMPTY -> {}
                FILLED -> {}
                LIT -> {
                    it.fuel -= TICK_DELTA
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