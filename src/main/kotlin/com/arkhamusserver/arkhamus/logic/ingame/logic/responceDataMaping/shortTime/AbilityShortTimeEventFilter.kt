package com.arkhamusserver.arkhamus.logic.ingame.logic.responceDataMaping.shortTime

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.VisibilityByTagsHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.enums.ingame.ShortTimeEventType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.RedisTimeEventState
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.model.redis.RedisShortTimeEvent
import org.springframework.stereotype.Component

@Component
class AbilityShortTimeEventFilter(
    private val userLocationHandler: UserLocationHandler,
    private val visibilityByTagsHandler: VisibilityByTagsHandler,
) : SpecificShortTimeEventFilter {

    companion object {
        private val shortTimeEventType = ShortTimeEventType.ABILITY_CAST
    }

    override fun filter(
        events: List<RedisShortTimeEvent>,
        user: RedisGameUser,
        zones: List<LevelZone>,
        data: GlobalGameData
    ): List<RedisShortTimeEvent> {
        return events.filter {
            it.type == shortTimeEventType &&
                    it.timeLeft > 0 &&
                    it.state == RedisTimeEventState.ACTIVE &&
                    fitSource(findSource(it.sourceId, data), user, data)
        }
    }

    private fun fitSource(
        source: RedisGameUser?,
        myUser: RedisGameUser,
        data: GlobalGameData
    ): Boolean {
        if (source == null) return false
        if (!userLocationHandler.userCanSeeTarget(myUser, source, data.levelGeometryData)) return false
        if (!visibilityByTagsHandler.userCanSeeTarget(myUser, source)) return false
        return true
    }

    private fun findSource(sourceId: Long?, data: GlobalGameData): RedisGameUser? =
        data.users[sourceId]

}