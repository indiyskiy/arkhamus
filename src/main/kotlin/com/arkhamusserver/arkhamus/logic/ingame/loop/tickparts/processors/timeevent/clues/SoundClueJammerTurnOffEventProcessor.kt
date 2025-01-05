package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.clues

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.processors.timeevent.TimeEventProcessor
import com.arkhamusserver.arkhamus.model.dataaccess.redis.clues.RedisSoundClueRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisTimeEvent
import org.springframework.stereotype.Component

@Component
class SoundClueJammerTurnOffEventProcessor(
    private val redisSoundClueRepository: RedisSoundClueRepository
) : TimeEventProcessor {
    override fun accept(type: RedisTimeEventType): Boolean =
        type == RedisTimeEventType.SOUND_CLUE_JAMMER_TURN_OFF


    override fun process(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        switchJammer(globalGameData, event, false)
    }

    override fun processStart(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        switchJammer(globalGameData, event, false)
    }

    override fun processEnd(
        event: RedisTimeEvent,
        globalGameData: GlobalGameData,
        currentGameTime: Long,
        timePassedMillis: Long
    ) {
        switchJammer(globalGameData, event, true)
    }

    private fun switchJammer(
        globalGameData: GlobalGameData,
        event: RedisTimeEvent,
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
        redisSoundClueRepository.save(clue)
    }
}