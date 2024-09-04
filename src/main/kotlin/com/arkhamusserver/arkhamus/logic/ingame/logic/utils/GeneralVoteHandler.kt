package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class GeneralVoteHandler(
    private val madnessHandler: UserMadnessHandler,
) {

    fun usersCanPossiblyVote(allUsers: Collection<RedisGameUser>) =
        madnessHandler.filterNotMad(allUsers)

    fun usersCanPossiblyVote(user: RedisGameUser) =
        !madnessHandler.isCompletelyMad(user)
}