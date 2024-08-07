package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState
import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState.AWAITING
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisUserQuestProgress")
data class RedisUserQuestProgress(
    @Id var id: String,
    @Indexed var gameId: Long,
    var questId: Long,
    var userId: Long,
    var questCurrentStep: Int = -1,
    var questState: UserQuestState = AWAITING,
    @TimeToLive val timeToLive: Long = 7200
)