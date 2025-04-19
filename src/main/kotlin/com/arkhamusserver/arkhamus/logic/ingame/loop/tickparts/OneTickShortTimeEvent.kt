package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.loop.requestprocessors.tech.LeaveTheGameRequestProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameShortTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameShortTimeEvent
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.min

@Component
class OneTickShortTimeEvent(
    private val shortTimeEventRepository: InGameShortTimeEventRepository,
) {

    companion object {
        private val logger = LoggingUtils.getLogger<OneTickShortTimeEvent>()
    }

    @Transactional
    fun processShortTimeEvents(
        timeEvents: List<InGameShortTimeEvent>,
        timePassedMillis: Long
    ): List<InGameShortTimeEvent> {
        val inGameTimeEvents: List<InGameShortTimeEvent> = timeEvents.mapNotNull { event ->
            val timeAdd = min(event.timeLeft, timePassedMillis)
            if (event.state == InGameTimeEventState.ACTIVE) {
                processActiveEvent(event, timeAdd)
            } else {
                null
            }
        }
        return inGameTimeEvents
    }

    private fun processActiveEvent(
        event: InGameShortTimeEvent,
        timeAdd: Long,
    ): InGameShortTimeEvent {
        event.timePast += timeAdd
        event.timeLeft -= timeAdd
        if (event.timeLeft > 0) {
            shortTimeEventRepository.save(event)
        } else {
            logger.info("end of life of event ${event.type}")
            event.state = InGameTimeEventState.PAST
            shortTimeEventRepository.delete(event)
        }
        return event
    }

}