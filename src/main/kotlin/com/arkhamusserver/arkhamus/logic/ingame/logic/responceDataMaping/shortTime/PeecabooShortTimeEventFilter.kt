package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import org.springframework.stereotype.Component

@Component
class PeecabooShortTimeEventFilter() : SpecificShortTimeEventFilter {

    companion object {
        private val shortTimeEventTypes = setOf(
            ShortTimeEventType.PEEKABOO_CURSE_ACTIVATED_CONTAINER,
            ShortTimeEventType.PEEKABOO_CURSE_ACTIVATED_CRAFTER,
        )
    }

    override fun accept(event: RedisShortTimeEvent): Boolean =
        event.type in shortTimeEventTypes


    override fun canSee(
        event: RedisShortTimeEvent,
        user: RedisGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): Boolean =
        true

}