package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.GameEndLogic
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.GameEndReason
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import org.springframework.stereotype.Component

@Component
class GodAwakenEventProcessor(
    private val gameEndLogic: GameEndLogic,
) : TimeEventProcessor {
    override fun accept(type: InGameTimeEventType): Boolean =
        type == InGameTimeEventType.GOD_AWAKEN

    override fun processStart(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {

    }

    override fun process(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {

    }

    override fun processEnd(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        gameEndLogic.endTheGame(globalGameData.game, globalGameData.users, GameEndReason.GOD_AWAKEN)
    }

}