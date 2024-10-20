package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("RedisQuestReward")
data class RedisQuestReward(
    @Id var id: String,
    var rewardType: RewardType,
    var rewardAmount: Int = 0,
    var rewardItem: Int?,
    @Indexed var gameId: Long,
    var questId: Long,
    var userId: Long,
    var questProgressId: String,
    var creationGameTime: Long,
)