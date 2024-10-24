package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.RitualHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AltarVotingEventProcessor(
    private val ritualHandler: RitualHandler,
) : TimeEventProcessor {
    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.ALTAR_VOTING

    override fun processStart(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {

    }

    override fun process(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {

    }

    @Transactional
    override fun processEnd(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        globalGameData.altarPolling?.let { altarPolling ->
            val quorum = ritualHandler.gotQuorum(globalGameData.users.values, altarPolling)
            if (quorum != null) {
                ritualHandler.lockTheGod(
                    quorum,
                    globalGameData.altars.values.toList(),
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