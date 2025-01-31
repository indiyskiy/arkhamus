package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.clues

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.TimeEventProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.clues.InGameSoundClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameTimeEventType
import com.arkhamusserver.arkhamus.model.ingame.InGameTimeEvent
import org.springframework.stereotype.Component

@Component
class SoundClueJammerTurnOffEventProcessor(
    private val inGameSoundClueRepository: InGameSoundClueRepository
) : TimeEventProcessor {
    override fun accept(type: InGameTimeEventType): Boolean =
        type == InGameTimeEventType.SOUND_CLUE_JAMMER_TURN_OFF


    override fun process(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        switchJammer(globalGameData, event, false)
    }

    override fun processStart(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        switchJammer(globalGameData, event, false)
    }

    override fun processEnd(
        event: InGameTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        switchJammer(globalGameData, event, true)
    }

    private fun switchJammer(
        globalGameData: GlobalGameData,
        event: InGameTimeEvent,
        turnedOn: Boolean
    ) {
        val clue = globalGameData.clues.sound.first {
            it.soundClueJammers.any {
                it.inGameId == event.targetObjectId
            }
        }
        val jammer = clue.soundClueJammers.first {
            it.inGameId == event.targetObjectId
        }
        jammer.turnedOn = turnedOn
        inGameSoundClueRepository.save(clue)
    }
}