package com.arkhamusserver.arkhamus.logic.ingame.logic

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState.ONGOING
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarState.VOTING
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventState.ACTIVE
import com.arkhamusserver.arkhamus.model.enums.ingame.RedisTimeEventType.*
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import org.springframework.stereotype.Component

@Component
class GodVoteHandler {
    fun canBeStarted(
        altarHolder: RedisAltarHolder,
        altar: RedisAltar?,
        ongoingEvents: List<OngoingEvent>
    ): Boolean {
        return (altarHolder.state == MapAltarState.OPEN) &&
                altar != null &&
                !ongoingEvents.any {
                    it.event.type in listOf(
                        ALTAR_VOTING,
                        RITUAL_GOING,
                        ALTAR_VOTING_COOLDOWN
                    ) && it.event.state == ACTIVE
                }
    }

    fun canIVote(
        altarPolling: RedisAltarPolling?,
        altarHolder: RedisAltarHolder?,
        user: UserAccount
    ) = isVoteProcessOpen(altarPolling, altarHolder) &&
            ((altarPolling?.userVotes?.get(user.id)) == null)

    fun isVoteProcessOpen(
        altarPolling: RedisAltarPolling?,
        altarHolder: RedisAltarHolder?
    ) =
        (altarPolling?.state == ONGOING) &&
                (altarHolder?.state == VOTING)
}