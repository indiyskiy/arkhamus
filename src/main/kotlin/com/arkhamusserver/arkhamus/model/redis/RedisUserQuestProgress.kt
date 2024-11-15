package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.UserQuestState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.UserQuestState.AWAITING

data class RedisUserQuestProgress(
    override var id: String,
    override var gameId: Long,
    var questId: Long,
    var userId: Long,
    var questCurrentStep: Int = -1,
    var questState: UserQuestState = AWAITING,
    var creationGameTime: Long,
    var readGameTime: Long? = null,
    var acceptanceGameTime: Long? = null,
    var finishGameTime: Long? = null,
) : RedisGameEntity