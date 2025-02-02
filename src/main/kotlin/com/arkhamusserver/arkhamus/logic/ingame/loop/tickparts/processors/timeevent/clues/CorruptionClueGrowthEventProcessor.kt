package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.clues

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.TimeEventProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameTimeEventRepository
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameCorruptionClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InGameTimeEventState
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameCorruptionClue
import org.springframework.stereotype.Component

@Component
class CorruptionClueGrowthEventProcessor(
    private val inGameCorruptionClueRepository: InGameCorruptionClueRepository,
    private val inGameTimeEventRepository: InGameTimeEventRepository
) : TimeEventProcessor {
    override fun accept(type: InGameTimeEventType): Boolean =
        type == InGameTimeEventType.CORRUPTION_CLUE_GROWTH


    override fun process(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        growCorruption(globalGameData, event, timePassedMillis, true)
    }

    override fun processStart(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        growCorruption(globalGameData, event, timePassedMillis, true)
    }

    override fun processEnd(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        growCorruption(globalGameData, event, timePassedMillis, false)
    }

    private fun growCorruption(
        globalGameData: GlobalGameData,
        event: InGameTimeEvent,
        timePassedMillis: Long,
        turnedOn: Boolean
    ) {
        val clue = globalGameData.clues.corruption.first {
            it.inGameId() == event.targetObjectId
        }
        if (!turnedOn) {
            nullify(clue)
        } else {
            push(event, clue, timePassedMillis)
        }
    }

    private fun push(
        event: InGameTimeEvent,
        clue: InGameCorruptionClue,
        timePassedMillis: Long
    ) {
        clue.timeFromStart += timePassedMillis
        if (clue.timeFromStart >= clue.totalTimeUntilNullify) {
            nullify(clue)
            if (event.state == InGameTimeEventState.ACTIVE) {
                endEvent(event)
            }
        } else {
            inGameCorruptionClueRepository.save(clue)
        }
    }

    private fun nullify(clue: InGameCorruptionClue) {
        clue.castedAbilityUsers = emptySet()
        clue.timeFromStart = 0L
        inGameCorruptionClueRepository.save(clue)
    }

    private fun endEvent(event: InGameTimeEvent) {
        event.state = InGameTimeEventState.PAST
        event.timePast += event.timeLeft
        event.timeLeft = 0L
        inGameTimeEventRepository.delete(event)
    }
}