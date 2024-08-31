package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class GeneralVoteHandler(
    private val madnessHandler: UserMadnessHandler,
) {

    fun userCanPossiblyVote(allUsers: Collection<RedisGameUser>) =
        madnessHandler.filterNotMad(allUsers)

    fun userCanPossiblyVote(user: RedisGameUser) =
        !madnessHandler.isCompletelyMad(user)
}