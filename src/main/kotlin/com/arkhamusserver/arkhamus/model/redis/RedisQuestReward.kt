package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

data class RedisQuestReward(
    override var id: String,
    var rewardType: RewardType,
    var rewardAmount: Int = 0,
    var rewardItem: Int?,
    override var gameId: Long,
    var questId: Long,
    var userId: Long,
    var questProgressId: String,
    var creationGameTime: Long,
): RedisGameEntity