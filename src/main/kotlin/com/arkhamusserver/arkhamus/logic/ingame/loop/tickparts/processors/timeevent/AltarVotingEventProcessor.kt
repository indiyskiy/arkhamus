package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.ritual.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AltarVotingEventProcessor(
    private val ritualHandler: RitualHandler,
) : TimeEventProcessor {
    override fun accept(type: InGameTimeEventType): Boolean =
        type == InGameTimeEventType.ALTAR_VOTING

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

    @Transactional
    override fun processEnd(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        globalGameData.altarPolling?.let { altarPolling ->
            val quorum = ritualHandler.gotQuorum(globalGameData.users.values, altarPolling)
            if (quorum != null) {
                ritualHandler.lockTheGod(
                    quorum,
                    globalGameData.altars,
                    altarPolling,
                    globalGameData.altarHolder,
                    globalGameData.timeEvents,
                    globalGameData.game
                )
            } else {
                ritualHandler.failRitualStartCooldown(
                    globalGameData.altarHolder,
                    altarPolling,
                    globalGameData.timeEvents,
                    globalGameData.game
                )
            }
        }
    }

}