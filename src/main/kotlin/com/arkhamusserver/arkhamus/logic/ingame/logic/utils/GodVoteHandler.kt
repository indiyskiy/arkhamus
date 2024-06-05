package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarPollingState.ONGOING
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarState
import com.arkhamusserver.arkhamus.model.enums.ingame.MapAltarState.VOTING
import com.arkhamusserver.arkhamus.model.redis.RedisAltar
import com.arkhamusserver.arkhamus.model.redis.RedisAltarHolder
import com.arkhamusserver.arkhamus.model.redis.RedisAltarPolling
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GodVoteHandler {

    companion object {
        var logger: Logger = LoggerFactory.getLogger(InventoryHandler::class.java)
    }

    fun canBeStarted(
        altarHolder: RedisAltarHolder,
        altar: RedisAltar?,
        ongoingEvents: List<OngoingEvent>
    ): Boolean {
        return getAltarIsOpen(altarHolder) &&  getAltarExist(altar)
    }

    fun canVote(
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

    private fun getAltarExist(altar: RedisAltar?) = altar != null

    private fun getAltarIsOpen(altarHolder: RedisAltarHolder) =
        altarHolder.state == MapAltarState.OPEN

}