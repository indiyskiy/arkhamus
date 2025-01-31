package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.model.ingame.InRamGame
import org.springframework.stereotype.Component

@Component
class OneTickTick() {
    fun updateNextTick(game: InRamGame): Long {
        game.serverTimeCurrentTick = System.currentTimeMillis()
        val timePassedMillis = game.serverTimeCurrentTick - game.serverTimeLastTick
        game.currentTick += 1
        game.globalTimer += timePassedMillis
        return timePassedMillis
    }
}