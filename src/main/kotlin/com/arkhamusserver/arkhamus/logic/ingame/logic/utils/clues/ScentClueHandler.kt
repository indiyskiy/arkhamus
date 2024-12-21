package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.clues

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.redis.clues.RedisScentClue
import com.arkhamusserver.arkhamus.model.redis.interfaces.WithTrueIngameId
import org.springframework.stereotype.Component

@Component
class ScentClueHandler {
    fun isTargetScentBad(target: WithTrueIngameId, data: GlobalGameData): Boolean {
        val godScent = data.game.god.getTypes().contains(Clue.SCENT)
        if(!godScent){
            return false
        }
        if(target is RedisScentClue){
            return target.scent
        }
        return false
    }

}