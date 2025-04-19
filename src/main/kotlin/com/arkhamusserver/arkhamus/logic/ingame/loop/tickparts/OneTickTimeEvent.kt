package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.TimeEventProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import org.slf4j.Logger
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Component
class OneTickTimeEvent(
    private val timeEventRepository: InGameTimeEventRepository,
    private val timeEventProcessors: List<TimeEventProcessor>
) {

    companion object {
        var logger: Logger = LoggingUtils.getLogger<OneTickTimeEvent>()
    }

    @Transactional
    fun processTimeEvents(
        globalGameData: GlobalGameData,
        timeEvents: List<InGameTimeEvent>,
        currentGameTime: Long,
        timePassedMillis: Long
    ): List<OngoingEvent> {
        val inGameTimeEvents: List<OngoingEvent> = timeEvents.mapNotNull { event ->
            val timeAdd = min(event.timeLeft, timePassedMillis)
            if (event.state == InGameTimeEventState.ACTIVE) {
                processActiveEvent(event, globalGameData, timeAdd, currentGameTime)
            } else {
                null
            }
        }
        return inGameTimeEvents
    }

    private fun processActiveEvent(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        timeAdd: Long,
        currentGameTime: Long,
    ): OngoingEvent {
        if (event.timePast == 0L) {
            applyStartProcessors(event, globalGameData, currentGameTime)
        }
        event.timePast += timeAdd
        event.timeLeft -= timeAdd
        if (event.timeLeft > 0) {
            applyProcessors(event, globalGameData, currentGameTime, timeAdd)
            timeEventRepository.save(event)
        } else {
            applyEndProcessors(event, globalGameData, currentGameTime, timeAdd)
            event.state = InGameTimeEventState.PAST
            timeEventRepository.delete(event)
        }
        return OngoingEvent(
            event = event,
        )
    }

    private fun applyStartProcessors(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long
    ) {
        timeEventProcessors
            .filter {
                it.accept(event.type)
            }
            .forEach {
                it.processStart(event, globalGameData, currentGameTime, 0)
            }
    }

    private fun applyEndProcessors(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        timeEventProcessors
            .filter {
                it.accept(event.type)
            }
            .forEach {
                it.processEnd(event, globalGameData, currentGameTime, timePassedMillis)
            }
    }

    private fun applyProcessors(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        timeEventProcessors
            .filter {
                it.accept(event.type)
            }
            .forEach {
                it.process(event, globalGameData, currentGameTime, timePassedMillis)
            }
    }
}
