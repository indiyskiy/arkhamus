package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item

data class InGameQuestReward(
    override var id: String,
    var rewardType: RewardType,
    var rewardAmount: Int = 0,
    var rewardItem: Item? = null,
    override var gameId: Long,
    var questId: Long,
    var userId: Long,
    var questProgressId: String,
    var creationGameTime: Long,
) : InGameEntity