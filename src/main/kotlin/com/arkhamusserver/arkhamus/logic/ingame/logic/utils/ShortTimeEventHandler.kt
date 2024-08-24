package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime.SpecificShortTimeEventFilter
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.dataaccess.redis.RedisShortTimeEventRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class ShortTimeEventHandler(
    private val specificShortTimeEventFilters: List<SpecificShortTimeEventFilter>,
    private val redisShortTimeEventRepository: RedisShortTimeEventRepository,
) {
    fun filter(
        events: List<RedisShortTimeEvent>,
        user: RedisGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): List<RedisShortTimeEvent> {
        return specificShortTimeEventFilters.map {
            it.filter(
                events,
                user,
                zones,
                data
            )
        }.flatten()
    }

    fun createCastAbilityEvent(ability: Ability, userId: Long, gameId: Long, globalTimer: Long) {
        redisShortTimeEventRepository.save(
            RedisShortTimeEvent(
                id = Generators.timeBasedEpochGenerator().generate().toString(),
                gameId = gameId,
                sourceId = userId,
                xLocation = null,
                yLocation = null,
                timeStart = globalTimer,
                timePast = 0,
                timeLeft = ShortTimeEventType.ABILITY_CAST.getTime(),
                type = ShortTimeEventType.ABILITY_CAST,
                state = RedisTimeEventState.ACTIVE,
            )
        )
    }

}