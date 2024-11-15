package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType

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
) : RedisGameEntity