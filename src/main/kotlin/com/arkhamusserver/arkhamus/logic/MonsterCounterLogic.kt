package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.MonsterCounter
import org.springframework.stereotype.Component


@Component
class MonsterCounterLogic {
    fun changeCounter(gameSession: GameSession, monsterCounter: MonsterCounter): MonsterCounter {
      //do something
        return monsterCounter
    }

}